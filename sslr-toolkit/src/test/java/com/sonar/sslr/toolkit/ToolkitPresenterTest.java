/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ToolkitPresenterTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void checkInitializedBad() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("the view must be set prior to any other method call");

    ToolkitPresenter presenter = new ToolkitPresenter();
    presenter.checkInitialized();
  }

  @Test
  public void checkInitializedGood() {
    ToolkitPresenter presenter = new ToolkitPresenter();
    presenter.setView(mock(ToolkitView.class));
    presenter.checkInitialized();
  }

  @Test
  public void onButtonClick() {
    ToolkitView view = mock(ToolkitView.class);
    ToolkitPresenter presenter = new ToolkitPresenter();
    presenter.setView(view);

    presenter.onButtonClick();
    verify(view).showDialog("The button was clicked!");
  }

}
