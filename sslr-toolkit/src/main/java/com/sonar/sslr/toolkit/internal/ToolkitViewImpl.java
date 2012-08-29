/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit.internal;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.toolkit.CssLoader;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

public class ToolkitViewImpl extends JFrame implements ToolkitView {

  private static final long serialVersionUID = 1L;
  private final static TreeModel EMPTY_TREE_MODEL = new DefaultTreeModel(null);

  public final ToolkitPresenter presenter;

  private final JTabbedPane tabbedPane = new JTabbedPane();

  private final JTextArea xmlTextArea = new JTextArea();
  private final JScrollPane xmlScrollPane = new JScrollPane(xmlTextArea);

  private final JTree astTree = new JTree();
  private final JScrollPane astTreeScrollPane = new JScrollPane(astTree);

  private final JLabel sourceCodeLabel = new JLabel(" Source Code");
  private final JEditorPane sourceCodeEditorPane = new JEditorPane();
  private final JScrollPane sourceCodeEditorScrollPane = new JScrollPane(sourceCodeEditorPane);
  private final JButton sourceCodeOpenButton = new JButton();
  private final JButton sourceCodeParseButton = new JButton();
  private final JPanel sourceCodeButtonsPanel = new JPanel();
  private final JPanel sourceCodePanel = new JPanel(new BorderLayout(0, 2));

  private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sourceCodePanel, tabbedPane);

  private final JPanel southPanel = new JPanel(new BorderLayout(0, 2));

  private final JLabel xpathLabel = new JLabel("  XPath query");
  private final JTextArea xpathTextArea = new JTextArea();
  private final JScrollPane xpathTextAreaScrollPane = new JScrollPane(xpathTextArea);
  private final JPanel xpathPanel = new JPanel(new BorderLayout(10, 2));

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

    setLayout(new BorderLayout(0, 5));

    astTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

    tabbedPane.setTabPlacement(JTabbedPane.TOP);
    tabbedPane.add("Abstract Syntax Tree", astTreeScrollPane);
    tabbedPane.add("XML", xmlScrollPane);

    sourceCodeEditorPane.setContentType("text/html");
    sourceCodeEditorPane.setEditable(true);
    ((DefaultCaret) sourceCodeEditorPane.getCaret()).setUpdatePolicy(DefaultCaret.UPDATE_WHEN_ON_EDT);

    sourceCodeOpenButton.setText("Open Source File");
    sourceCodeOpenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        presenter.onSourceCodeOpenButtonClick();
      }
    });

    sourceCodeParseButton.setText("Parse Source Code");
    sourceCodeParseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        presenter.onSourceCodeParseButtonClick();
      }
    });

    sourceCodeButtonsPanel.add(sourceCodeOpenButton);
    sourceCodeButtonsPanel.add(sourceCodeParseButton);

    sourceCodePanel.add(sourceCodeLabel, BorderLayout.NORTH);
    sourceCodePanel.add(sourceCodeEditorScrollPane, BorderLayout.CENTER);
    sourceCodePanel.add(sourceCodeButtonsPanel, BorderLayout.SOUTH);

    splitPane.setDividerLocation(getWidth() / 2);
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

  public File pickFileToParse() {
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile();
    } else {
      return null;
    }
  }

  public void displayHighlightedSourceCode(String htmlHighlightedSourceCode) {
    StringBuffer sb = new StringBuffer();
    sb.append("<html><head><style type=\"text/css\">");
    sb.append(CssLoader.getCss());
    sb.append("</style></head><body><pre class=\"code\" id=\"code\">");
    sb.append(htmlHighlightedSourceCode);
    sb.append("</pre></body></html>");

    sourceCodeEditorPane.setText(sb.toString());
  }

  // TODO Remove as this method is now unused
  public void setSourceCodeTextCursor(int offset) {
    sourceCodeEditorPane.setCaretPosition(getValidSourceCodeTextOffset(offset));
  }

  private int getValidSourceCodeTextOffset(int offset) {
    offset = Math.max(0, offset);
    offset = offset + getCodeElementStartOffset();
    offset = Math.min(getCodeElementEndOffset(), offset);

    return offset;
  }

  private int getCodeElementStartOffset() {
    HTMLDocument htmlDocument = (HTMLDocument) sourceCodeEditorPane.getDocument();
    Element codeElement = htmlDocument.getElement("code");
    return codeElement.getStartOffset();
  }

  private int getCodeElementEndOffset() {
    HTMLDocument htmlDocument = (HTMLDocument) sourceCodeEditorPane.getDocument();
    Element codeElement = htmlDocument.getElement("code");
    return codeElement.getEndOffset();
  }

  public void displayAst(AstNode astNode) {
    if (astNode == null) {
      astTree.setModel(EMPTY_TREE_MODEL);
    } else {
      TreeNode treeNode = getTreeNode(astNode);
      astTree.setModel(new DefaultTreeModel(treeNode));
    }
  }

  private DefaultMutableTreeNode getTreeNode(AstNode astNode) {
    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(astNode);

    if (astNode.hasChildren()) {
      for (AstNode childAstNode : astNode.getChildren()) {
        treeNode.add(getTreeNode(childAstNode));
      }
    }
    else if (astNode.hasToken() && astNode.getToken().hasTrivia()) {
      for (Trivia trivia : astNode.getToken().getTrivia()) {
        DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(trivia);

        if (trivia.hasPreprocessingDirective()) {
          childTreeNode.add(getTreeNode(trivia.getPreprocessingDirective().getAst()));
        }

        treeNode.add(childTreeNode);
      }
    }

    return treeNode;
  }

  public void displayXml(String xml) {
    xmlTextArea.setText(xml);
  }

  public Point getScrollbarPosition() {
    int x = sourceCodeEditorScrollPane.getHorizontalScrollBar().getValue();
    int y = sourceCodeEditorScrollPane.getVerticalScrollBar().getValue();

    return new Point(x, y);
  }

  public void scrollTo(final Point point) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        sourceCodeEditorScrollPane.getHorizontalScrollBar().setValue(point.x);
        sourceCodeEditorScrollPane.getVerticalScrollBar().setValue(point.y);
      }
    });
  }

  public String getSourceCode() {
    int startOffset = getCodeElementStartOffset();
    int endOffset = getCodeElementEndOffset();

    try {
      return sourceCodeEditorPane.getText(startOffset, endOffset - startOffset - 1);
    } catch (BadLocationException e) {
      throw new RuntimeException(e);
    }
  }

}
