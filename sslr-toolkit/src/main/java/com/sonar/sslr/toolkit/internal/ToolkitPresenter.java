/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit.internal;

import com.google.common.annotations.VisibleForTesting;

import java.awt.Point;
import java.io.File;
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

  public void run(String title) {
    checkInitialized();

    view.setTitle(title);
    view.displayHighlightedSourceCode("");
    view.displayAst(null);
    view.displayXml("");
    view.run();
  }

  public void onSourceCodeOpenButtonClick() {
    File fileToParse = view.pickFileToParse();
    if (fileToParse != null) {
      model.setSourceCode(fileToParse, Charset.defaultCharset());
      view.displayHighlightedSourceCode(model.getHighlightedSourceCode());
      view.displayAst(model.getAstNode());
      view.displayXml(model.getXml());
      view.scrollTo(new Point(0, 0));
    }
  }

}
