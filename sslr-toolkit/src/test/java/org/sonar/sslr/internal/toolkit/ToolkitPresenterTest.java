/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.internal.toolkit;

import org.sonar.sslr.internal.toolkit.SourceCodeModel;
import org.sonar.sslr.internal.toolkit.ToolkitPresenter;
import org.sonar.sslr.internal.toolkit.ToolkitView;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.nio.charset.Charset;

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

    ToolkitPresenter presenter = new ToolkitPresenter(mock(SourceCodeModel.class));
    presenter.checkInitialized();
  }

  @Test
  public void checkInitializedGood() {
    ToolkitPresenter presenter = new ToolkitPresenter(mock(SourceCodeModel.class));
    presenter.setView(mock(ToolkitView.class));
    presenter.checkInitialized();
  }

  @Test
  public void initUncaughtExceptionsHandler() throws InterruptedException {
    ToolkitView view = mock(ToolkitView.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(SourceCodeModel.class));
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
  public void run() {
    ToolkitView view = mock(ToolkitView.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(SourceCodeModel.class));
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
  public void runFailsWithoutView() {
    thrown.expect(IllegalStateException.class);
    new ToolkitPresenter(mock(SourceCodeModel.class)).run("foo");
  }

  @Test
  public void onSourceCodeOpenButtonClick() {
    ToolkitView view = mock(ToolkitView.class);
    File file = mock(File.class);
    when(view.pickFileToParse()).thenReturn(file);
    SourceCodeModel model = mock(SourceCodeModel.class);
    AstNode astNode = mock(AstNode.class);
    when(model.getHighlightedSourceCode()).thenReturn("my_mocked_highlighted_source_code");
    when(model.getAstNode()).thenReturn(astNode);
    when(model.getXml()).thenReturn("my_mocked_xml");

    ToolkitPresenter presenter = new ToolkitPresenter(model);
    presenter.setView(view);

    presenter.onSourceCodeOpenButtonClick();

    verify(view).pickFileToParse();

    verify(view).clearConsole();
    verify(model).setSourceCode(file, Charset.defaultCharset());
    verify(view).displayHighlightedSourceCode("my_mocked_highlighted_source_code");
    verify(view).displayAst(astNode);
    verify(view).displayXml("my_mocked_xml");
    verify(view).scrollSourceCodeTo(new Point(0, 0));
    verify(view).setFocusOnAbstractSyntaxTreeView();
    verify(view).enableXPathEvaluateButton();
  }

  @Test
  public void onSourceCodeOpenButtonClickNoOperationWhenNoFile() {
    ToolkitView view = mock(ToolkitView.class);
    when(view.pickFileToParse()).thenReturn(null);

    SourceCodeModel model = mock(SourceCodeModel.class);

    ToolkitPresenter presenter = new ToolkitPresenter(model);
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

    ToolkitPresenter presenter = new ToolkitPresenter(model);
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

    ToolkitPresenter presenter = new ToolkitPresenter(model);
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

    ToolkitPresenter presenter = new ToolkitPresenter(model);
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

    ToolkitPresenter presenter = new ToolkitPresenter(model);
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

    ToolkitPresenter presenter = new ToolkitPresenter(mock(SourceCodeModel.class));
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

    ToolkitPresenter presenter = new ToolkitPresenter(mock(SourceCodeModel.class));
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
    when(view.getSelectedAstNodes()).thenReturn(Lists.newArrayList(firstAstNode, secondAstNode));

    ToolkitPresenter presenter = new ToolkitPresenter(mock(SourceCodeModel.class));
    presenter.setView(view);

    presenter.onAstSelectionChanged();

    verify(view).clearSourceCodeHighlights();

    verify(view).highlightSourceCode(firstAstNode);
    verify(view).highlightSourceCode(secondAstNode);

    verify(view).scrollSourceCodeTo(firstAstNode);

    verify(view, never()).scrollSourceCodeTo(secondAstNode);
  }

}
