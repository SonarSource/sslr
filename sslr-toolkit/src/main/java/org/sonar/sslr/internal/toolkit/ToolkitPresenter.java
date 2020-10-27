/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.internal.toolkit;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;
import org.sonar.sslr.toolkit.ConfigurationModel;
import org.sonar.sslr.toolkit.ConfigurationProperty;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ToolkitPresenter {

  private final ConfigurationModel configurationModel;
  private final SourceCodeModel model;
  private ToolkitView view = null;

  public ToolkitPresenter(ConfigurationModel configurationModel, SourceCodeModel model) {
    this.configurationModel = configurationModel;
    this.model = model;
  }

  public void setView(ToolkitView view) {
    Objects.requireNonNull(view);
    this.view = view;
  }

  // @VisibleForTesting
  void checkInitialized() {
    if (view == null) {
      throw new IllegalStateException("the view must be set before the presenter can be ran");
    }
  }

  // @VisibleForTesting
  void initUncaughtExceptionsHandler() {
    Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);

        view.appendToConsole(result.toString());
        view.setFocusOnConsoleView();
      }
    });
  }

  // @VisibleForTesting
  void initConfigurationTab() {
    for (ConfigurationProperty configurationProperty : configurationModel.getProperties()) {
      view.addConfigurationProperty(configurationProperty.getName(), configurationProperty.getDescription());
      view.setConfigurationPropertyValue(configurationProperty.getName(), configurationProperty.getValue());
    }
  }

  public void run(String title) {
    checkInitialized();

    initUncaughtExceptionsHandler();

    view.setTitle(title);
    view.displayHighlightedSourceCode("");
    view.displayAst(null);
    view.displayXml("");
    view.disableXPathEvaluateButton();

    initConfigurationTab();

    view.run();
  }

  public void onSourceCodeOpenButtonClick() {
    File fileToParse = view.pickFileToParse();
    if (fileToParse != null) {
      view.clearConsole();
      try {
        view.displayHighlightedSourceCode(new String(Files.readAllBytes(Paths.get(fileToParse.getPath())), configurationModel.getCharset()));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      model.setSourceCode(fileToParse, configurationModel.getCharset());
      view.displayHighlightedSourceCode(model.getHighlightedSourceCode());
      view.displayAst(model.getAstNode());
      view.displayXml(model.getXml());
      view.scrollSourceCodeTo(new Point(0, 0));
      view.setFocusOnAbstractSyntaxTreeView();
      view.enableXPathEvaluateButton();
    }
  }

  public void onSourceCodeParseButtonClick() {
    view.clearConsole();
    String sourceCode = view.getSourceCode();
    model.setSourceCode(sourceCode);
    Point sourceCodeScrollbarPosition = view.getSourceCodeScrollbarPosition();
    view.displayHighlightedSourceCode(model.getHighlightedSourceCode());
    view.displayAst(model.getAstNode());
    view.displayXml(model.getXml());
    view.scrollSourceCodeTo(sourceCodeScrollbarPosition);
    view.setFocusOnAbstractSyntaxTreeView();
    view.enableXPathEvaluateButton();
  }

  public void onXPathEvaluateButtonClick() {
    String xpath = view.getXPath();
    AstNodeXPathQuery<Object> xpathQuery = AstNodeXPathQuery.create(xpath);

    view.clearConsole();
    view.clearAstSelections();
    view.clearSourceCodeHighlights();

    AstNode firstAstNode = null;
    for (Object resultObject : xpathQuery.selectNodes(model.getAstNode())) {
      if (resultObject instanceof AstNode) {
        AstNode resultAstNode = (AstNode) resultObject;

        if (firstAstNode == null) {
          firstAstNode = resultAstNode;
        }

        view.selectAstNode(resultAstNode);
        view.highlightSourceCode(resultAstNode);
      }
    }

    view.scrollAstTo(firstAstNode);
    view.scrollSourceCodeTo(firstAstNode);

    view.setFocusOnAbstractSyntaxTreeView();
  }

  public void onSourceCodeKeyTyped() {
    view.displayAst(null);
    view.displayXml("");
    view.clearSourceCodeHighlights();
    view.disableXPathEvaluateButton();
  }

  public void onSourceCodeTextCursorMoved() {
    view.clearAstSelections();
    AstNode astNode = view.getAstNodeFollowingCurrentSourceCodeTextCursorPosition();
    view.selectAstNode(astNode);
    view.scrollAstTo(astNode);
  }

  public void onAstSelectionChanged() {
    view.clearSourceCodeHighlights();

    AstNode firstAstNode = null;

    for (AstNode astNode : view.getSelectedAstNodes()) {
      if (firstAstNode == null) {
        firstAstNode = astNode;
      }

      view.highlightSourceCode(astNode);
    }

    view.scrollSourceCodeTo(firstAstNode);
  }

  public void onConfigurationPropertyFocusLost(String name) {
    ConfigurationProperty configurationProperty = getConfigurationPropertyByName(name);
    if (configurationProperty == null) {
      throw new IllegalArgumentException("No such configuration property: " + name);
    }

    String newValueCandidate = view.getConfigurationPropertyValue(name);
    String errorMessage = configurationProperty.validate(newValueCandidate);

    view.setConfigurationPropertyErrorMessage(configurationProperty.getName(), errorMessage);

    if ("".equals(errorMessage)) {
      configurationProperty.setValue(newValueCandidate);
      configurationModel.setUpdatedFlag();
    } else {
      view.setFocusOnConfigurationPropertyField(name);
      view.setFocusOnConfigurationView();
    }
  }

  private ConfigurationProperty getConfigurationPropertyByName(String name) {
    for (ConfigurationProperty configurationProperty : configurationModel.getProperties()) {
      if (name.equals(configurationProperty.getName())) {
        return configurationProperty;
      }
    }

    return null;
  }

}
