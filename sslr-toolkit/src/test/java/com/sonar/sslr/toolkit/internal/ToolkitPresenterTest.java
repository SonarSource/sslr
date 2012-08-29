/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit.internal;

import com.sonar.sslr.api.AstNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.Point;
import java.io.File;
import java.nio.charset.Charset;

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
  public void run() {
    ToolkitView view = mock(ToolkitView.class);

    ToolkitPresenter presenter = new ToolkitPresenter(mock(SourceCodeModel.class));
    presenter.setView(view);

    presenter.run("my_mocked_title");
    verify(view).setTitle("my_mocked_title");
    verify(view).displayHighlightedSourceCode("");
    verify(view).displayAst(null);
    verify(view).displayXml("");
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
    verify(model).setSourceCode(file, Charset.defaultCharset());
    verify(view).displayHighlightedSourceCode("my_mocked_highlighted_source_code");
    verify(view).displayAst(astNode);
    verify(view).displayXml("my_mocked_xml");
    verify(view).scrollTo(new Point(0, 0));
  }

  @Test
  public void onSourceCodeOpenButtonClickNoOperationWhenNoFile() {
    ToolkitView view = mock(ToolkitView.class);
    when(view.pickFileToParse()).thenReturn(null);

    SourceCodeModel model = mock(SourceCodeModel.class);

    ToolkitPresenter presenter = new ToolkitPresenter(model);
    presenter.setView(view);

    presenter.onSourceCodeOpenButtonClick();
    verify(model, never()).setSourceCode(any(File.class), any(Charset.class));
    verify(view, never()).displayHighlightedSourceCode(anyString());
    verify(view, never()).displayAst(any(AstNode.class));
    verify(view, never()).displayXml(anyString());
    verify(view, never()).scrollTo(any(Point.class));
  }

}
