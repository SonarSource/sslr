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
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.sslr.toolkit.ConfigurationModel;
import org.sonar.sslr.toolkit.ConfigurationProperty;

import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ToolkitPresenterTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void checkInitializedBad() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("the view must be set before the presenter can be ran");

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.checkInitialized();
  }

  @Test
  public void checkInitializedGood() {
    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.setView(mock(ToolkitView.class));
    presenter.checkInitialized();
  }

  @Test
  public void initUncaughtExceptionsHandler() throws InterruptedException {
    ToolkitView view = mock(ToolkitView.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.setView(view);

    presenter.initUncaughtExceptionsHandler();

    UncaughtExceptionHandler uncaughtExceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
    assertThat(uncaughtExceptionHandler instanceof ThreadGroup).isFalse();

    Throwable e = mock(Throwable.class);

    uncaughtExceptionHandler.uncaughtException(null, e);
    verify(e).printStackTrace(any(PrintWriter.class));
    verify(view).appendToConsole(anyString());
    verify(view).setFocusOnConsoleView();
  }

  @Test
  public void initConfigurationTab() {
    ToolkitView view = mock(ToolkitView.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.setView(view);
    presenter.initConfigurationTab();

    verify(view, never()).addConfigurationProperty(Mockito.anyString(), Mockito.anyString());
    verify(view, never()).setConfigurationPropertyValue(Mockito.anyString(), Mockito.anyString());

    ConfigurationProperty property1 = mock(ConfigurationProperty.class);
    when(property1.getName()).thenReturn("property1");
    when(property1.getDescription()).thenReturn("description1");
    when(property1.getValue()).thenReturn("default1");

    ConfigurationProperty property2 = mock(ConfigurationProperty.class);
    when(property2.getName()).thenReturn("property2");
    when(property2.getDescription()).thenReturn("description2");
    when(property2.getValue()).thenReturn("default2");

    ConfigurationModel configurationModel = mock(ConfigurationModel.class);
    when(configurationModel.getProperties()).thenReturn(Arrays.asList(property1, property2));
    presenter = new ToolkitPresenter(configurationModel, mock(SourceCodeModel.class));
    presenter.setView(view);
    presenter.initConfigurationTab();

    verify(view).addConfigurationProperty("property1", "description1");
    verify(view).setConfigurationPropertyValue("property1", "default1");
    verify(view).addConfigurationProperty("property2", "description2");
    verify(view).setConfigurationPropertyValue("property2", "default2");
  }

  @Test
  public void run() {
    ToolkitView view = mock(ToolkitView.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.setView(view);

    presenter.run("my_mocked_title");

    assertThat(Thread.currentThread().getUncaughtExceptionHandler() instanceof ThreadGroup).isFalse();
    verify(view).setTitle("my_mocked_title");
    verify(view).displayHighlightedSourceCode("");
    verify(view).displayAst(null);
    verify(view).displayXml("");
    verify(view).disableXPathEvaluateButton();
    verify(view).run();
  }

  @Test
  public void run_should_call_initConfigurationTab() {
    ToolkitView view = mock(ToolkitView.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.setView(view);
    presenter.run("my_mocked_title");
    verify(view, never()).addConfigurationProperty(Mockito.anyString(), Mockito.anyString());

    ConfigurationModel configurationModel = mock(ConfigurationModel.class);
    when(configurationModel.getProperties()).thenReturn(Collections.singletonList(mock(ConfigurationProperty.class)));
    presenter = new ToolkitPresenter(configurationModel, mock(SourceCodeModel.class));
    presenter.setView(view);
    presenter.run("my_mocked_title");
    verify(view).addConfigurationProperty(any(), any());
  }

  @Test
  public void runFailsWithoutView() {
    thrown.expect(IllegalStateException.class);
    new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class)).run("foo");
  }

  @Test
  public void onSourceCodeOpenButtonClick() {
    ToolkitView view = mock(ToolkitView.class);
    File file = new File("src/test/resources/parse_error.txt");
    when(view.pickFileToParse()).thenReturn(file);
    SourceCodeModel model = mock(SourceCodeModel.class);
    AstNode astNode = mock(AstNode.class);
    when(model.getHighlightedSourceCode()).thenReturn("my_mocked_highlighted_source_code");
    when(model.getAstNode()).thenReturn(astNode);
    when(model.getXml()).thenReturn("my_mocked_xml");

    ToolkitPresenter presenter = new ToolkitPresenter((ConfigurationModel) when(mock(ConfigurationModel.class).getCharset()).thenReturn(StandardCharsets.UTF_8).getMock(), model);
    presenter.setView(view);

    presenter.onSourceCodeOpenButtonClick();

    verify(view).pickFileToParse();

    verify(view).clearConsole();
    verify(view).displayHighlightedSourceCode("my_mocked_highlighted_source_code");
    verify(model).setSourceCode(file, StandardCharsets.UTF_8);
    verify(view).displayAst(astNode);
    verify(view).displayXml("my_mocked_xml");
    verify(view).scrollSourceCodeTo(new Point(0, 0));
    verify(view).setFocusOnAbstractSyntaxTreeView();
    verify(view).enableXPathEvaluateButton();
  }

  @Test
  public void onSourceCodeOpenButtonClick_with_parse_error_should_clear_console_and_display_code() {
    ToolkitView view = mock(ToolkitView.class);
    File file = new File("src/test/resources/parse_error.txt");
    when(view.pickFileToParse()).thenReturn(file);
    SourceCodeModel model = mock(SourceCodeModel.class);
    Mockito.doThrow(new RuntimeException("Parse error")).when(model).setSourceCode(Mockito.any(File.class), Mockito.any(Charset.class));

    ToolkitPresenter presenter = new ToolkitPresenter((ConfigurationModel) when(mock(ConfigurationModel.class).getCharset()).thenReturn(StandardCharsets.UTF_8).getMock(), model);
    presenter.setView(view);

    try {
      presenter.onSourceCodeOpenButtonClick();
      throw new AssertionError("Expected an exception");
    } catch (RuntimeException e) {
      verify(view).clearConsole();
      verify(view).displayHighlightedSourceCode("parse_error.txt");
    }
  }

  @Test
  public void onSourceCodeOpenButtonClick_should_no_operation_when_no_file() {
    ToolkitView view = mock(ToolkitView.class);
    when(view.pickFileToParse()).thenReturn(null);

    SourceCodeModel model = mock(SourceCodeModel.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), model);
    presenter.setView(view);

    presenter.onSourceCodeOpenButtonClick();

    verify(view).pickFileToParse();

    verify(view, never()).clearConsole();
    verify(model, never()).setSourceCode(any(File.class), any(Charset.class));
    verify(view, never()).displayHighlightedSourceCode(anyString());
    verify(view, never()).displayAst(any(AstNode.class));
    verify(view, never()).displayXml(anyString());
    verify(view, never()).scrollSourceCodeTo(any(Point.class));
    verify(view, never()).enableXPathEvaluateButton();
  }

  @Test
  public void onSourceCodeParseButtonClick() {
    ToolkitView view = mock(ToolkitView.class);
    when(view.getSourceCode()).thenReturn("my_mocked_source");
    Point point = mock(Point.class);
    when(view.getSourceCodeScrollbarPosition()).thenReturn(point);
    SourceCodeModel model = mock(SourceCodeModel.class);
    when(model.getHighlightedSourceCode()).thenReturn("my_mocked_highlighted_source_code");
    AstNode astNode = mock(AstNode.class);
    when(model.getAstNode()).thenReturn(astNode);
    when(model.getXml()).thenReturn("my_mocked_xml");

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), model);
    presenter.setView(view);

    presenter.onSourceCodeParseButtonClick();

    verify(view).clearConsole();
    verify(view).getSourceCode();
    verify(model).setSourceCode("my_mocked_source");
    verify(view).displayHighlightedSourceCode("my_mocked_highlighted_source_code");
    view.displayAst(astNode);
    view.displayXml("my_mocked_xml");
    view.scrollSourceCodeTo(point);
    verify(view).setFocusOnAbstractSyntaxTreeView();
    verify(view).enableXPathEvaluateButton();
  }

  @Test
  public void onXPathEvaluateButtonClickAstNodeResults() {
    ToolkitView view = mock(ToolkitView.class);
    when(view.getXPath()).thenReturn("//foo");
    SourceCodeModel model = mock(SourceCodeModel.class);
    AstNode astNode = new AstNode(GenericTokenType.IDENTIFIER, "foo", null);
    when(model.getAstNode()).thenReturn(astNode);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), model);
    presenter.setView(view);

    presenter.onXPathEvaluateButtonClick();

    verify(view).clearAstSelections();
    verify(view).clearSourceCodeHighlights();

    verify(view).selectAstNode(astNode);
    verify(view).highlightSourceCode(astNode);

    verify(view).scrollAstTo(astNode);
  }

  @Test
  public void onXPathEvaluateButtonClickScrollToFirstAstNode() {
    ToolkitView view = mock(ToolkitView.class);
    when(view.getXPath()).thenReturn("//foo");
    SourceCodeModel model = mock(SourceCodeModel.class);
    AstNode astNode = new AstNode(GenericTokenType.IDENTIFIER, "foo", null);
    AstNode childAstNode = new AstNode(GenericTokenType.IDENTIFIER, "foo", null);
    astNode.addChild(childAstNode);
    when(model.getAstNode()).thenReturn(astNode);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), model);
    presenter.setView(view);

    presenter.onXPathEvaluateButtonClick();

    verify(view).scrollAstTo(astNode);
    verify(view, never()).scrollAstTo(childAstNode);

    verify(view).scrollSourceCodeTo(astNode);
    verify(view, never()).scrollSourceCodeTo(childAstNode);
  }

  @Test
  public void onXPathEvaluateButtonClickStringResult() throws Exception {
    ToolkitView view = mock(ToolkitView.class);
    when(view.getXPath()).thenReturn("//foo/@tokenValue");
    SourceCodeModel model = mock(SourceCodeModel.class);
    Token token = Token.builder()
        .setType(GenericTokenType.IDENTIFIER)
        .setValueAndOriginalValue("bar")
        .setURI(new URI("tests://unittest"))
        .setLine(1)
        .setColumn(1)
        .build();
    AstNode astNode = new AstNode(GenericTokenType.IDENTIFIER, "foo", token);
    when(model.getAstNode()).thenReturn(astNode);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), model);
    presenter.setView(view);

    presenter.onXPathEvaluateButtonClick();

    verify(view).clearConsole();
    verify(view).clearAstSelections();
    verify(view).clearSourceCodeHighlights();

    verify(view, never()).selectAstNode(any(AstNode.class));
    verify(view, never()).highlightSourceCode(any(AstNode.class));

    verify(view).scrollAstTo(null);
    verify(view).scrollSourceCodeTo((AstNode) null);

    verify(view).setFocusOnAbstractSyntaxTreeView();
  }

  @Test
  public void onSourceCodeKeyTyped() {
    ToolkitView view = mock(ToolkitView.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.setView(view);

    presenter.onSourceCodeKeyTyped();

    verify(view).displayAst(null);
    verify(view).displayXml("");
    verify(view).clearSourceCodeHighlights();
    verify(view).disableXPathEvaluateButton();
  }

  @Test
  public void onSourceCodeTextCursorMoved() {
    ToolkitView view = mock(ToolkitView.class);
    AstNode astNode = mock(AstNode.class);
    when(view.getAstNodeFollowingCurrentSourceCodeTextCursorPosition()).thenReturn(astNode);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.setView(view);

    presenter.onSourceCodeTextCursorMoved();

    verify(view).clearAstSelections();
    verify(view).selectAstNode(astNode);
    verify(view).scrollAstTo(astNode);
  }

  @Test
  public void onAstSelectionChanged() {
    ToolkitView view = mock(ToolkitView.class);
    AstNode firstAstNode = mock(AstNode.class);
    AstNode secondAstNode = mock(AstNode.class);
    when(view.getSelectedAstNodes()).thenReturn(Arrays.asList(firstAstNode, secondAstNode));

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.setView(view);

    presenter.onAstSelectionChanged();

    verify(view).clearSourceCodeHighlights();

    verify(view).highlightSourceCode(firstAstNode);
    verify(view).highlightSourceCode(secondAstNode);

    verify(view).scrollSourceCodeTo(firstAstNode);

    verify(view, never()).scrollSourceCodeTo(secondAstNode);
  }

  @Test
  public void onConfigurationPropertyFocusLost_when_validation_successes() {
    ToolkitView view = mock(ToolkitView.class);

    ConfigurationProperty property = mock(ConfigurationProperty.class);
    when(property.getName()).thenReturn("name");
    when(property.getDescription()).thenReturn("description");
    when(view.getConfigurationPropertyValue("name")).thenReturn("foo");
    when(property.validate("foo")).thenReturn("");

    ConfigurationModel configurationModel = mock(ConfigurationModel.class);
    when(configurationModel.getProperties()).thenReturn(Collections.singletonList(property));
    ToolkitPresenter presenter = new ToolkitPresenter(configurationModel, mock(SourceCodeModel.class));
    presenter.setView(view);

    presenter.onConfigurationPropertyFocusLost("name");

    verify(view).setConfigurationPropertyErrorMessage("name", "");
    verify(view, never()).setFocusOnConfigurationPropertyField(Mockito.anyString());
    verify(view, never()).setFocusOnConfigurationView();
    verify(property).setValue("foo");
    verify(configurationModel).setUpdatedFlag();
  }

  @Test
  public void onConfigurationPropertyFocusLost_when_validation_fails() {
    ToolkitView view = mock(ToolkitView.class);

    ConfigurationProperty property = mock(ConfigurationProperty.class);
    when(property.getName()).thenReturn("name");
    when(property.getDescription()).thenReturn("description");
    when(view.getConfigurationPropertyValue("name")).thenReturn("foo");
    when(property.validate("foo")).thenReturn("The value foo is forbidden!");

    ConfigurationModel configurationModel = mock(ConfigurationModel.class);
    when(configurationModel.getProperties()).thenReturn(Collections.singletonList(property));
    ToolkitPresenter presenter = new ToolkitPresenter(configurationModel, mock(SourceCodeModel.class));
    presenter.setView(view);

    presenter.onConfigurationPropertyFocusLost("name");

    verify(view).setConfigurationPropertyErrorMessage("name", "The value foo is forbidden!");
    verify(view).setFocusOnConfigurationPropertyField("name");
    verify(view).setFocusOnConfigurationView();
    verify(property, never()).setValue("foo");
    verify(configurationModel, never()).setUpdatedFlag();
  }

  @Test
  public void onConfigurationPropertyFocusLost_with_invalid_name() {
    ToolkitView view = mock(ToolkitView.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(ConfigurationModel.class), mock(SourceCodeModel.class));
    presenter.setView(view);

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("No such configuration property: name");

    presenter.onConfigurationPropertyFocusLost("name");
  }

}
