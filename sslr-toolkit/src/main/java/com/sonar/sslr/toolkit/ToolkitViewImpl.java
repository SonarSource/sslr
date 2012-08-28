/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.google.common.base.Preconditions.checkNotNull;

public class ToolkitViewImpl extends JFrame implements ToolkitView {

  private static final long serialVersionUID = 1L;

  public final ToolkitPresenter presenter;

  private final JButton button = new JButton("Click me!");

  public ToolkitViewImpl(ToolkitPresenter presenter) {
    checkNotNull(presenter);
    this.presenter = presenter;

    initComponents();
  }

  private void initComponents() {
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        presenter.onButtonClick();
      }
    });
    add(button);

    setSize(200, 100);
  }

  public void run() {
    setVisible(true);
  }

  public void showDialog(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

}
