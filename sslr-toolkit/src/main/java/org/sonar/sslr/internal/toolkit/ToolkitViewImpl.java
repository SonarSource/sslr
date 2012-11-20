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

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;

import javax.annotation.Nullable;
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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ToolkitViewImpl extends JFrame implements ToolkitView {

  private static final long serialVersionUID = 1L;
  private static final TreeModel EMPTY_TREE_MODEL = new DefaultTreeModel(null);

  public final transient ToolkitPresenter presenter;

  private final JTabbedPane tabbedPane = new JTabbedPane();

  private final JTree astTree = new JTree();
  private final JScrollPane astTreeScrollPane = new JScrollPane(astTree);

  private final JTextArea xmlTextArea = new JTextArea();
  private final JScrollPane xmlScrollPane = new JScrollPane(xmlTextArea);

  private final JTextArea consoleTextArea = new JTextArea();
  private final JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);

  private final JPanel configurationInnerPanel = new JPanel(new GridBagLayout());
  private final JPanel configurationOuterPanel = new JPanel(new BorderLayout());
  private final JScrollPane configurationScrollPane = new JScrollPane(configurationOuterPanel);
  private final Map<String, ConfigurationPropertyPanel> configurationPropertiesPanels = Maps.newHashMap();

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

  private transient LineOffsets lineOffsets = null;
  private final transient DefaultHighlighter.DefaultHighlightPainter highlighter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);

  private boolean sourceCodeTextCursorMovedEventDisabled = false;
  private boolean astSelectionEventDisabled = false;

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
    astTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent event) {
        if (!astSelectionEventDisabled) {
          presenter.onAstSelectionChanged();
        }
      }
    });

    consoleTextArea.setEditable(false);

    tabbedPane.setTabPlacement(JTabbedPane.TOP);
    tabbedPane.add("Abstract Syntax Tree", astTreeScrollPane);
    tabbedPane.add("XML", xmlScrollPane);
    tabbedPane.add("Console", consoleScrollPane);
    tabbedPane.add("Configuration", configurationScrollPane);

    configurationOuterPanel.add(configurationInnerPanel, BorderLayout.NORTH);
    configurationOuterPanel.add(Box.createGlue(), BorderLayout.CENTER);

    sourceCodeEditorPane.setContentType("text/html");
    sourceCodeEditorPane.setEditable(true);
    ((DefaultCaret) sourceCodeEditorPane.getCaret()).setUpdatePolicy(DefaultCaret.UPDATE_WHEN_ON_EDT);
    sourceCodeEditorPane.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        presenter.onSourceCodeKeyTyped();
      }
    });
    sourceCodeEditorPane.addCaretListener(new CaretListener() {
      public void caretUpdate(CaretEvent e) {
        if (!sourceCodeTextCursorMovedEventDisabled) {
          presenter.onSourceCodeTextCursorMoved();
        }
      }
    });

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
    xpathButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        presenter.onXPathEvaluateButtonClick();
      }
    });
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
    try {
      sourceCodeTextCursorMovedEventDisabled = true;

      checkNotNull(htmlHighlightedSourceCode);

      StringBuffer sb = new StringBuffer();
      sb.append("<html><head><style type=\"text/css\">");
      sb.append(CssLoader.getCss());
      sb.append("</style></head><body><pre class=\"code\" id=\"code\">");
      sb.append(htmlHighlightedSourceCode);
      sb.append("</pre></body></html>");

      sourceCodeEditorPane.setText(sb.toString());
      lineOffsets = new LineOffsets(getSourceCode());
    } finally {
      sourceCodeTextCursorMovedEventDisabled = false;
    }
  }

  public void displayAst(@Nullable AstNode astNode) {
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
    checkNotNull(xml);

    xmlTextArea.setText(xml);
  }

  public Point getSourceCodeScrollbarPosition() {
    int x = sourceCodeEditorScrollPane.getHorizontalScrollBar().getValue();
    int y = sourceCodeEditorScrollPane.getVerticalScrollBar().getValue();

    return new Point(x, y);
  }

  public void scrollSourceCodeTo(final Point point) {
    checkNotNull(point);

    // http://stackoverflow.com/questions/8789371/java-jtextpane-jscrollpane-de-activate-automatic-scrolling
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
      throw Throwables.propagate(e);
    }
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

  public String getXPath() {
    return xpathTextArea.getText();
  }

  public void selectAstNode(AstNode astNode) {
    if (astNode != null) {
      try {
        astSelectionEventDisabled = true;
        DefaultMutableTreeNode treeNode = getAstTreeNodeWithGivenUserObject((DefaultMutableTreeNode) astTree.getModel().getRoot(), astNode);
        astTree.getSelectionModel().addSelectionPath(new TreePath(treeNode.getPath()));
      } finally {
        astSelectionEventDisabled = false;
      }
    }
  }

  private DefaultMutableTreeNode getAstTreeNodeWithGivenUserObject(DefaultMutableTreeNode treeNode, Object userObject) {
    if (treeNode.getUserObject().equals(userObject)) {
      return treeNode;
    } else {
      for (int i = 0; i < treeNode.getChildCount(); i++) {
        DefaultMutableTreeNode treeNodeWithUserObject = getAstTreeNodeWithGivenUserObject((DefaultMutableTreeNode) treeNode.getChildAt(i), userObject);
        if (treeNodeWithUserObject != null) {
          return treeNodeWithUserObject;
        }
      }

      return null;
    }
  }

  public void highlightSourceCode(AstNode astNode) {
    checkNotNull(astNode);

    Token startToken = astNode.getToken();
    Token endToken = astNode.getLastToken();

    int startOffset = getValidDocumentOffsetFromSourceCodeOffset(lineOffsets.getStartOffset(startToken));
    int endOffset = getValidDocumentOffsetFromSourceCodeOffset(lineOffsets.getEndOffset(endToken));

    try {
      sourceCodeEditorPane.getHighlighter().addHighlight(startOffset, endOffset, highlighter);
    } catch (BadLocationException e) {
      throw Throwables.propagate(e);
    }
  }

  private int getValidDocumentOffsetFromSourceCodeOffset(int offset) {
    offset = Math.max(offset, 0);
    offset += getCodeElementStartOffset();
    offset = Math.min(offset, getCodeElementEndOffset());

    return offset;
  }

  public void clearAstSelections() {
    try {
      astSelectionEventDisabled = true;
      astTree.getSelectionModel().clearSelection();
    } finally {
      astSelectionEventDisabled = false;
    }
  }

  public void scrollAstTo(@Nullable AstNode astNode) {
    if (astNode != null) {
      DefaultMutableTreeNode treeNode = getAstTreeNodeWithGivenUserObject((DefaultMutableTreeNode) astTree.getModel().getRoot(), astNode);
      astTree.scrollPathToVisible(new TreePath(treeNode.getPath()));
    }
  }

  public void clearSourceCodeHighlights() {
    sourceCodeEditorPane.getHighlighter().removeAllHighlights();
  }

  public void scrollSourceCodeTo(@Nullable AstNode astNode) {
    if (astNode != null) {
      int visibleLines = sourceCodeEditorPane.getVisibleRect().height / sourceCodeEditorPane.getFontMetrics(sourceCodeEditorPane.getFont()).getHeight();
      int line = astNode.getToken().getLine() + visibleLines / 2;

      try {
        sourceCodeEditorPane.scrollRectToVisible(sourceCodeEditorPane.modelToView(0));
        sourceCodeEditorPane.scrollRectToVisible(sourceCodeEditorPane.modelToView(lineOffsets.getOffset(line, 0)));
      } catch (BadLocationException e) {
        throw Throwables.propagate(e);
      }
    }
  }

  public void disableXPathEvaluateButton() {
    xpathButton.setEnabled(false);
  }

  public void enableXPathEvaluateButton() {
    xpathButton.setEnabled(true);
  }

  @Nullable
  public AstNode getAstNodeFollowingCurrentSourceCodeTextCursorPosition() {
    int currentOffset = sourceCodeEditorPane.getCaretPosition() - getCodeElementStartOffset();

    return getFollowingAstNode((DefaultMutableTreeNode) astTree.getModel().getRoot(), currentOffset);
  }

  private AstNode getFollowingAstNode(DefaultMutableTreeNode treeNode, int offset) {
    AstNode followingAstNode = null;

    if (treeNode != null) {
      Enumeration<DefaultMutableTreeNode> enumeration = ((DefaultMutableTreeNode) astTree.getModel().getRoot()).breadthFirstEnumeration();

      int nearestOffsetSoFar = Integer.MAX_VALUE;
      while (enumeration.hasMoreElements()) {
        DefaultMutableTreeNode childTreeNode = enumeration.nextElement();
        if (childTreeNode.getUserObject() instanceof AstNode) {
          AstNode astNode = (AstNode) childTreeNode.getUserObject();

          if (astNode.hasToken()) {
            Token token = astNode.getToken();
            int tokenOffset = lineOffsets.getStartOffset(token);

            if (tokenOffset >= offset && tokenOffset < nearestOffsetSoFar) {
              nearestOffsetSoFar = tokenOffset;
              followingAstNode = astNode;
            }
          }
        }
      }
    }

    return followingAstNode;
  }

  public List<AstNode> getSelectedAstNodes() {
    List<AstNode> acc = Lists.newArrayList();

    TreePath[] selectedPaths = astTree.getSelectionPaths();
    if (selectedPaths != null) {
      for (TreePath selectedPath : selectedPaths) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();

        Object userObject = treeNode.getUserObject();
        if (userObject instanceof AstNode) {
          AstNode astNode = (AstNode) userObject;
          acc.add(astNode);
        }
      }
    }

    return acc;
  }

  public void appendToConsole(String message) {
    consoleTextArea.append(message);
  }

  public void setFocusOnConsoleView() {
    tabbedPane.setSelectedComponent(consoleScrollPane);
  }

  public void setFocusOnAbstractSyntaxTreeView() {
    tabbedPane.setSelectedComponent(astTreeScrollPane);
  }

  public void clearConsole() {
    consoleTextArea.setText("");
  }

  public void addConfigurationProperty(final String name, String description) {
    ConfigurationPropertyPanel configurationPropertyPanel = new ConfigurationPropertyPanel(name, description);

    configurationPropertyPanel.getValueTextField().addFocusListener(new FocusAdapter() {

      @Override
      public void focusLost(FocusEvent e) {
        presenter.onConfigurationPropertyFocusLost(name);
      }

    });

    configurationPropertiesPanels.put(name, configurationPropertyPanel);

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.weightx = 1;
    constraints.gridx = 0;
    constraints.anchor = GridBagConstraints.NORTH;

    configurationInnerPanel.add(configurationPropertyPanel.getPanel(), constraints);
  }

  public String getConfigurationPropertyValue(String name) {
    return configurationPropertiesPanels.get(name).getValueTextField().getText();
  }

  public void setConfigurationPropertyValue(String name, String value) {
    configurationPropertiesPanels.get(name).getValueTextField().setText(value);
  }

  public void setConfigurationPropertyErrorMessage(String name, String errorMessage) {
    configurationPropertiesPanels.get(name).getErrorMessageLabel().setText(errorMessage);
  }

  public void setFocusOnConfigurationPropertyField(String name) {
    configurationPropertiesPanels.get(name).getValueTextField().requestFocus();
  }

  public void setFocusOnConfigurationView() {
    tabbedPane.setSelectedComponent(configurationScrollPane);
  }

}
