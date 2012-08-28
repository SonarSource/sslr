/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import java.awt.BorderLayout;

import static com.google.common.base.Preconditions.checkNotNull;

public class ToolkitViewImpl extends JFrame implements ToolkitView {

  private static final long serialVersionUID = 1L;

  public final ToolkitPresenter presenter;

  private final JTabbedPane tabbedPane = new JTabbedPane();

  private final JTextArea xmlTextArea = new JTextArea();
  private final JScrollPane xmlScrollPane = new JScrollPane(xmlTextArea);

  private final JTree astTree = new JTree();
  private final JScrollPane astTreeScrollPane = new JScrollPane(astTree);

  private final JLabel codeLabel = new JLabel(" Source Code");
  private final JEditorPane codeEditorPane = new JEditorPane();
  private final JScrollPane codeEditorScrollPane = new JScrollPane(codeEditorPane);
  private final JButton openButton = new JButton();
  private final JButton parseButton = new JButton();
  private final JPanel codeButtonsPanel = new JPanel();
  private final JPanel codePanel = new JPanel(new BorderLayout());

  private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codePanel, tabbedPane);

  private final JPanel southPanel = new JPanel(new BorderLayout());

  private final JLabel xpathLabel = new JLabel("  XPath query:");
  private final JTextArea xpathTextArea = new JTextArea();
  private final JScrollPane xpathTextAreaScrollPane = new JScrollPane(xpathTextArea);
  private final JPanel xpathPanel = new JPanel(new BorderLayout(10, 5));

  private final JFileChooser fileChooser = new JFileChooser();
  private final JButton xpathButton = new JButton();
  private final JPanel xpathButtonPanel = new JPanel();

  public ToolkitViewImpl(ToolkitPresenter presenter) {
    checkNotNull(presenter);
    this.presenter = presenter;

    initComponents();
  }

  private void initComponents() {
    setSize(1000, 700);
    setDefaultCloseOperation(ToolkitViewImpl.EXIT_ON_CLOSE);

    setLayout(new BorderLayout(0, 20));

    astTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

    tabbedPane.setTabPlacement(JTabbedPane.TOP);
    tabbedPane.add("Abstract Syntax Tree", astTreeScrollPane);
    tabbedPane.add("XML", xmlScrollPane);

    codeEditorPane.setContentType("text/html");
    codeEditorPane.setEditable(true);

    openButton.setText("Open Source File");
    parseButton.setText("Parse Source Code");
    codeButtonsPanel.add(openButton);
    codeButtonsPanel.add(parseButton);

    codePanel.add(codeLabel, BorderLayout.NORTH);
    codePanel.add(codeEditorScrollPane, BorderLayout.CENTER);
    codePanel.add(codeButtonsPanel, BorderLayout.SOUTH);

    splitPane.setDividerLocation(500);
    add(splitPane, BorderLayout.CENTER);

    xpathPanel.add(xpathLabel, BorderLayout.NORTH);
    xpathPanel.add(Box.createHorizontalGlue(), BorderLayout.WEST);
    xpathTextArea.setText("//IDENTIFIER");
    xpathTextArea.setRows(8);
    xpathPanel.add(xpathTextAreaScrollPane, BorderLayout.CENTER);
    xpathPanel.add(Box.createHorizontalGlue(), BorderLayout.EAST);

    southPanel.add(xpathPanel, BorderLayout.NORTH);

    xpathButton.setText("Evaluate XPath");
    xpathButtonPanel.add(xpathButton);

    southPanel.add(xpathButtonPanel, BorderLayout.SOUTH);

    add(southPanel, BorderLayout.SOUTH);
  }

  public void run() {
    setVisible(true);
  }

  public void showDialog(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

}
