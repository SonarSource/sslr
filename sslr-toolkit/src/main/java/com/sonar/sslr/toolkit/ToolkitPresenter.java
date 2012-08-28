/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit;

import com.google.common.annotations.VisibleForTesting;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ToolkitPresenter {

  public ToolkitView view = null;

  public void setView(ToolkitView view) {
    checkNotNull(view);
    this.view = view;
  }

  @VisibleForTesting
  void checkInitialized() {
    checkState(view != null, "the view must be set prior to any other method call");
  }

  public void onButtonClick() {
    checkInitialized();
    view.showDialog("The button was clicked!");
  }

}
