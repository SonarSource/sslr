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
package com.sonar.sslr.toolkit;

import com.google.common.collect.Maps;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.PreprocessingDirective;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.colorizer.HtmlOptions;
import org.sonar.colorizer.HtmlRenderer;
import org.sonar.colorizer.Tokenizer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@SuppressWarnings("serial")
public class SsdkGui extends javax.swing.JFrame {

  private static final Logger LOG = LoggerFactory.getLogger(SsdkGui.class);
  private static final DefaultTreeModel EMPTY_TREE_MODEL = new DefaultTreeModel(null);

  private AstNode fileNode = null;

  private final JTabbedPane tabbedPane = new JTabbedPane();
  private final JTextArea xmlTextArea = new JTextArea();
  private final JTree astTree = new JTree();
  private final JScrollPane astTreeScrollPane = new JScrollPane(astTree);
  private final Map<Object, DefaultMutableTreeNode> userObjectToTreeNodeCache = Maps.newHashMap();
  private final JEditorPane codeEditor = new JEditorPane();
  private final JScrollPane codeEditorScrollPane = new JScrollPane(codeEditor);
  private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codeEditorScrollPane, tabbedPane);

  private final JPanel southPanel = new JPanel(new BorderLayout());

  private final JTextArea xpathTextArea = new JTextArea();
  private final JScrollPane xpathTextAreaScrollPane = new JScrollPane(xpathTextArea);
  private final JPanel xpathPanel = new JPanel(new BorderLayout(10, 5));

  private final JFileChooser fileChooser = new JFileChooser();
  private final JButton openButton = new JButton();
  private final JButton parseButton = new JButton();
  private final JButton xpathButton = new JButton();
  private final JPanel buttonsPanel = new JPanel();

  private final Offsets lineOffsets = new Offsets();
  private final Parser<? extends Grammar> parser;
  private final List<Tokenizer> colorizerTokenizers;
  private final HtmlRenderer htmlRenderer = new HtmlRenderer(new HtmlOptions(false, null, false));

  public SsdkGui(Parser<? extends Grammar> parser, List<Tokenizer> colorizerTokenizers) {
    this.parser = parser;
    this.colorizerTokenizers = colorizerTokenizers;

    setLayout(new BorderLayout(2, 2));
    setDefaultCloseOperation(SsdkGui.EXIT_ON_CLOSE);

    astTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    astTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent event) {
        highlightCodeForSelectedPaths();
        scrollCodeToFirstSelectedPath();
      }
    });

    tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
    tabbedPane.add("Abstract Syntax Tree", astTreeScrollPane);
    tabbedPane.add("XML", xmlTextArea);

    codeEditor.setContentType("text/html");
    codeEditor.setEditable(true);
    codeEditor.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent event) {
        showAstAndXml("");
      }
    });
    codeEditor.addCaretListener(new CaretListener() {
      public void caretUpdate(CaretEvent event) {
        astSelectPath();
        scrollAstToSelectedPath();
      }
    });

    splitPane.setDividerLocation(500);
    add(splitPane, BorderLayout.CENTER);

    xpathTextArea.setText("//IDENTIFIER");
    xpathTextArea.setRows(8);
    xpathPanel.add(new JLabel(" XPath query:"), BorderLayout.NORTH);
    xpathPanel.add(Box.createHorizontalGlue(), BorderLayout.WEST);
    xpathPanel.add(xpathTextAreaScrollPane, BorderLayout.CENTER);
    xpathPanel.add(Box.createHorizontalGlue(), BorderLayout.EAST);

    southPanel.add(xpathPanel, BorderLayout.NORTH);

    openButton.setText("Open file");
    openButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        int returnVal = fileChooser.showOpenDialog(SsdkGui.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

          File file = fileChooser.getSelectedFile();
          loadFromFile(file);
        }
      }
    });

    parseButton.setText("Parse text");
    parseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        String code = getCode();

        loadFromString(code);
      }
    });

    xpathButton.setText("Evaluate XPath");
    xpathButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (fileNode == null) {
          LOG.error("The code must be parsed before XPath queries can be evaluated");
        } else {
          evaluateXPath(xpathTextArea.getText());
        }
      }
    });

    buttonsPanel.add(openButton);
    buttonsPanel.add(parseButton);
    buttonsPanel.add(xpathButton);

    southPanel.add(buttonsPanel, BorderLayout.SOUTH);

    add(southPanel, BorderLayout.SOUTH);

    loadFromString("");
  }

  private String getCode() {
    HTMLDocument htmlDocument = (HTMLDocument) codeEditor.getDocument();
    Element codeElement = htmlDocument.getElement("code");

    int startOffset = codeElement.getStartOffset();
    int length = codeElement.getEndOffset() - startOffset - 1;

    try {
      return htmlDocument.getText(startOffset, length);
    } catch (BadLocationException e) {
      LOG.error("Error while getting the code", e);
      return "";
    }
  }

  private void evaluateXPath(String xpath) {
    astTree.clearSelection();
    codeEditor.getHighlighter().removeAllHighlights();

    try {
      AstNodeXPathQuery<Object> xpathQuery = AstNodeXPathQuery.create(xpath);

      for (Object object : xpathQuery.selectNodes(fileNode)) {
        if (object instanceof AstNode) {
          AstNode astNode = (AstNode) object;
          highlightCodeForAstNode(astNode);
        }
      }
    } catch (RuntimeException e) {
      LOG.error("Error while evaluating the XPath query", e);
    }
  }

  private void highlightCodeForSelectedPaths() {
    codeEditor.getHighlighter().removeAllHighlights();

    TreePath[] selectedPaths = astTree.getSelectionPaths();
    if (selectedPaths != null) {
      for (TreePath selectedPath : selectedPaths) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();

        AstNode astNode = getAstNodeFromUserObject(treeNode.getUserObject());
        highlightCodeForAstNode(astNode);
      }
    }
  }

  private void highlightCodeForAstNode(AstNode astNode) {
    try {
      Token firstToken = astNode.getToken();
      Token lastToken = astNode.getLastToken();

      codeEditor.getHighlighter().addHighlight(lineOffsets.getStartOffset(firstToken), lineOffsets.getEndOffset(lastToken),
          new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
    } catch (BadLocationException e) {
      LOG.error("Error while highlighting the code", e);
    }
  }

  private void scrollCodeToFirstSelectedPath() {
    TreePath selectedPath = astTree.getSelectionPath();

    if (selectedPath != null) {
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
      AstNode astNode = getAstNodeFromUserObject(treeNode.getUserObject());

      int visibleLines = codeEditor.getVisibleRect().height / codeEditor.getFontMetrics(codeEditor.getFont()).getHeight();
      int line = astNode.getToken().getLine() + visibleLines / 2;

      try {
        codeEditor.scrollRectToVisible(codeEditor.modelToView(0));
        codeEditor.scrollRectToVisible(codeEditor.modelToView(lineOffsets.getOffset(line, 0)));
      } catch (BadLocationException e) {
        LOG.error("Error while scrolling to the code", e);
      }
    }
  }

  private DefaultMutableTreeNode getParentFromUserObject(Object userObject) {
    DefaultMutableTreeNode treeNode = userObjectToTreeNodeCache.get(userObject);
    checkState(treeNode != null, "No tree node with the given user object was found");

    boolean isUnderTrivia = false;
    DefaultMutableTreeNode parentTreeNode = treeNode;
    while (!isUnderTrivia && parentTreeNode != null) {
      isUnderTrivia = parentTreeNode.getUserObject() instanceof Trivia;
      parentTreeNode = (DefaultMutableTreeNode) parentTreeNode.getParent();
    }

    return parentTreeNode;
  }

  private AstNode getAstNodeFromUserObject(Object userObject) {
    checkNotNull(userObject, "userObject cannot be null");

    DefaultMutableTreeNode parent = getParentFromUserObject(userObject);

    return (AstNode) (parent == null ? userObject : parent.getUserObject());
  }

  private void astSelectPath() {
    if (!EMPTY_TREE_MODEL.equals(astTree.getModel())) {
      int offset = codeEditor.getCaretPosition();
      int line = lineOffsets.getLineFromOffset(offset);
      int column = lineOffsets.getColumnFromOffsetAndLine(offset, line);

      int minimumOffset = Integer.MAX_VALUE;
      DefaultMutableTreeNode treeNode = null;
      Enumeration<DefaultMutableTreeNode> enumeration = ((DefaultMutableTreeNode) astTree.getModel().getRoot()).breadthFirstEnumeration();
      while (enumeration.hasMoreElements()) {
        DefaultMutableTreeNode treeNodeChild = enumeration.nextElement();
        if (getParentFromUserObject(treeNodeChild.getUserObject()) == null) {
          AstNode astNode = (AstNode) treeNodeChild.getUserObject();
          Token token = astNode.getToken();

          if ((token.getLine() > line || token.getLine() == line && token.getColumn() >= column) && lineOffsets.getStartOffset(token) < minimumOffset) {
            minimumOffset = lineOffsets.getStartOffset(token);
            treeNode = treeNodeChild;
          }
        }
      }

      astTree.clearSelection();

      if (treeNode != null) {
        astTree.addSelectionPath(new TreePath(treeNode.getPath()));
        highlightCodeForSelectedPaths();
      }
    }
  }

  private void scrollAstToSelectedPath() {
    TreePath selectionPath = astTree.getSelectionPath();
    if (selectionPath != null) {
      astTree.scrollPathToVisible(selectionPath);
    }
  }

  private void loadFromFile(File file) {
    try {
      loadFromString(FileUtils.readFileToString(file));
      codeEditor.setCaretPosition(0);
    } catch (IOException e) {
      LOG.error("Unable to load the code file '" + file.getAbsolutePath() + "'", e);
    }
  }

  private void loadFromString(String code) {
    showCode(code);
    lineOffsets.computeLineOffsets(code, codeEditor.getDocument().getEndPosition().getOffset());
    showAstAndXml(code);
  }

  private void showCode(String code) {
    StringBuffer sb = new StringBuffer();
    sb.append("<html><head><style type=\"text/css\">");
    sb.append(CssLoader.getCss());
    sb.append("</style></head><body><pre class=\"code\" id=\"code\">");
    sb.append(htmlRenderer.render(new StringReader(code), colorizerTokenizers));
    sb.append("</pre></body></html>");

    codeEditor.setText(sb.toString());
  }

  private void showAstAndXml(String code) {
    if (!EMPTY_TREE_MODEL.equals(astTree.getModel())) {
      fileNode = null;

      astTree.setModel(EMPTY_TREE_MODEL);
      userObjectToTreeNodeCache.clear();

      xmlTextArea.setText("");
    }

    if (code.length() > 0) {
      try {
        fileNode = parser.parse(code);
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(fileNode);
        userObjectToTreeNodeCache.put(fileNode, treeNode);

        addChildNodes(treeNode, fileNode);

        astTree.setModel(new DefaultTreeModel(treeNode));
        xmlTextArea.setText(AstXmlPrinter.print(fileNode));
      } catch (RecognitionException re) {
        fileNode = null;
        LOG.error("Unable to parse the code.", re);
      }
    }
  }

  private void addChildNodes(DefaultMutableTreeNode treeNode, AstNode astNode) {
    if (astNode.hasChildren()) {
      for (AstNode astNodeChild : astNode.getChildren()) {
        DefaultMutableTreeNode treeNodeChild = new DefaultMutableTreeNode(astNodeChild);
        userObjectToTreeNodeCache.put(astNodeChild, treeNodeChild);
        treeNode.add(treeNodeChild);
        addChildNodes(treeNodeChild, astNodeChild);
      }
    } else if (astNode.hasToken() && astNode.getToken().hasTrivia()) {
      for (Trivia trivia : astNode.getToken().getTrivia()) {
        DefaultMutableTreeNode treeNodeChild = new DefaultMutableTreeNode(trivia);
        userObjectToTreeNodeCache.put(trivia, treeNodeChild);
        treeNode.add(treeNodeChild);

        if (trivia.hasPreprocessingDirective()) {
          PreprocessingDirective directive = trivia.getPreprocessingDirective();
          DefaultMutableTreeNode treeNodeInnerChild = new DefaultMutableTreeNode(directive.getAst());
          userObjectToTreeNodeCache.put(directive.getAst(), treeNodeInnerChild);
          treeNodeChild.add(treeNodeInnerChild);
          addChildNodes(treeNodeInnerChild, directive.getAst());
        }
      }
    }
  }

  private void readObject(ObjectInputStream os) throws NotSerializableException {
    throw new NotSerializableException(getClass().getName());
  }

  private void writeObject(ObjectOutputStream os) throws NotSerializableException {
    throw new NotSerializableException(getClass().getName());
  }

}
