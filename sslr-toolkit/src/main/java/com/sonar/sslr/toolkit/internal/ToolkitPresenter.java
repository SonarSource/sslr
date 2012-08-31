/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit.internal;

import com.google.common.annotations.VisibleForTesting;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;

import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ToolkitPresenter {

  public final SourceCodeModel model;
  public ToolkitView view = null;

  public ToolkitPresenter(SourceCodeModel model) {
    this.model = model;
  }

  public void setView(ToolkitView view) {
    checkNotNull(view);
    this.view = view;
  }

  @VisibleForTesting
  void checkInitialized() {
    checkState(view != null, "the view must be set before the presenter can be ran");
  }

  @VisibleForTesting
  void initUncaughtExceptionsHandler() {
    Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      public void uncaughtException(Thread t, Throwable e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);

        view.appendToConsole(result.toString());
        view.setFocusOnConsoleView();
      }
    });
  }

  public void run(String title) {
    checkInitialized();

    initUncaughtExceptionsHandler();

    view.setTitle(title);
    view.displayHighlightedSourceCode("");
    view.displayAst(null);
    view.displayXml("");
    view.disableXPathEvaluateButton();
    view.run();
  }

  public void onSourceCodeOpenButtonClick() {
    File fileToParse = view.pickFileToParse();
    if (fileToParse != null) {
      model.setSourceCode(fileToParse, Charset.defaultCharset());
      view.displayHighlightedSourceCode(model.getHighlightedSourceCode());
      view.displayAst(model.getAstNode());
      view.displayXml(model.getXml());
      view.scrollSourceCodeTo(new Point(0, 0));
      view.enableXPathEvaluateButton();
    }
  }

  public void onSourceCodeParseButtonClick() {
    String sourceCode = view.getSourceCode();
    model.setSourceCode(sourceCode);
    Point sourceCodeScrollbarPosition = view.getSourceCodeScrollbarPosition();
    view.displayHighlightedSourceCode(model.getHighlightedSourceCode());
    view.displayAst(model.getAstNode());
    view.displayXml(model.getXml());
    view.scrollSourceCodeTo(sourceCodeScrollbarPosition);
    view.enableXPathEvaluateButton();
  }

  public void onXPathEvaluateButtonClick() {
    String xpath = view.getXPath();
    AstNodeXPathQuery<Object> xpathQuery = AstNodeXPathQuery.create(xpath);

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

}
