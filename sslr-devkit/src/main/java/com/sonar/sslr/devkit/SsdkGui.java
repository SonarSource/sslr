/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.devkit;

import com.google.common.collect.Maps;
import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.Parser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.colorizer.HtmlOptions;
import org.sonar.colorizer.HtmlRenderer;
import org.sonar.colorizer.Tokenizer;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
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
import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

@SuppressWarnings("serial")
public class SsdkGui extends javax.swing.JFrame {

  private static final String CSS_PATH = "/com/sonar/sslr/devkit/codeEditor.css";
  private static final Logger LOG = LoggerFactory.getLogger("DevKit");
  private static final DefaultTreeModel EMPTY_TREE_MODEL = new DefaultTreeModel(null);

  private final JFileChooser fileChooser = new JFileChooser();
  private final JButton openButton = new JButton();
  private final JButton parseButton = new JButton();
  private final JPanel buttonPanel = new JPanel();
  private final JTree astTree = new JTree();
  private final JScrollPane astTreeScrollPane = new JScrollPane(astTree);
  private final Map<Object, DefaultMutableTreeNode> userObjectToTreeNodeCache = Maps.newHashMap();
  private final JEditorPane codeEditor = new JEditorPane();
  private final JScrollPane codeEditorScrollPane = new JScrollPane(codeEditor);
  private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codeEditorScrollPane, astTreeScrollPane);

  private final Map<Integer, Integer> lineOffsets = Maps.newHashMap();
  private final transient Parser<? extends Grammar> parser;
  private final transient List<Tokenizer> colorizerTokenizers;
  private final transient HtmlRenderer htmlRenderer = new HtmlRenderer(new HtmlOptions(false, null, false));

  public SsdkGui(Parser<? extends Grammar> parser, List<Tokenizer> colorizerTokenizers) {
    this.parser = parser;
    this.colorizerTokenizers = colorizerTokenizers;

    setLayout(new BorderLayout(2, 2));
    setDefaultCloseOperation(SsdkGui.EXIT_ON_CLOSE);

    openButton.setText("Open file");
    openButton.addActionListener(new ActionListener() {
      @Override
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
      @Override
      public void actionPerformed(ActionEvent event) {
        String code = "";
        Document document = codeEditor.getDocument();
        if (document.getLength() > 0) {
          try {
            code = document.getText(1, document.getEndPosition().getOffset() - 1);
          } catch (BadLocationException e) {
            LOG.error("Error while reading code buffer", e);
          }
        }
        loadFromString(code);
      }
    });

    buttonPanel.add(openButton);
    buttonPanel.add(parseButton);
    add(buttonPanel, BorderLayout.PAGE_END);

    astTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    astTree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent event) {
        highlightSelectedPaths();
        scrollToFirstSelectedPath();
      }
    });

    codeEditor.setContentType("text/html");
    codeEditor.setEditable(true);
    codeEditor.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent event) {
        showAst("");
      }
    });
    codeEditor.addCaretListener(new CaretListener() {
      @Override
      public void caretUpdate(CaretEvent event) {
        selectPath();
        scrollToSelectedPath();
      }
    });

    splitPane.setDividerLocation(500);
    add(splitPane, BorderLayout.CENTER);

    loadFromString("");
  }

  private void highlightSelectedPaths() {
    codeEditor.getHighlighter().removeAllHighlights();

    TreePath[] selectedPaths = astTree.getSelectionPaths();
    if (selectedPaths != null) {
      for (TreePath selectedPath : selectedPaths) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();

        AstNode astNode = getAstNodeFromUserObject(treeNode.getUserObject());

        try {
          Token firstToken = astNode.getToken();
          Token lastToken = astNode.getLastToken();

          codeEditor.getHighlighter().addHighlight(getStartOffset(firstToken), getEndOffset(lastToken),
              new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
        } catch (BadLocationException e) {
          LOG.error("Error with the highlighter", e);
        }
      }
    }
  }

  private void scrollToFirstSelectedPath() {
    TreePath selectedPath = astTree.getSelectionPath();

    if (selectedPath != null) {
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
      AstNode astNode = getAstNodeFromUserObject(treeNode.getUserObject());

      int visibleLines = codeEditor.getVisibleRect().height / codeEditor.getFontMetrics(codeEditor.getFont()).getHeight();
      int line = astNode.getToken().getLine() + visibleLines / 2;

      try {
        codeEditor.scrollRectToVisible(codeEditor.modelToView(0));
        codeEditor.scrollRectToVisible(codeEditor.modelToView(getOffset(line, 0)));
      } catch (BadLocationException e) {
        LOG.error("Error with the scrolling", e);
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

  private void selectPath() {
    if (!EMPTY_TREE_MODEL.equals(astTree.getModel())) {
      int offset = codeEditor.getCaretPosition();
      int line = getLineFromOffset(offset);
      int column = getColumnFromOffsetAndLine(offset, line);

      int minimumOffset = Integer.MAX_VALUE;
      DefaultMutableTreeNode treeNode = null;
      Enumeration<DefaultMutableTreeNode> enumeration = ((DefaultMutableTreeNode) astTree.getModel().getRoot()).breadthFirstEnumeration();
      while (enumeration.hasMoreElements()) {
        DefaultMutableTreeNode treeNodeChild = enumeration.nextElement();
        if (getParentFromUserObject(treeNodeChild.getUserObject()) == null) {
          AstNode astNode = (AstNode) treeNodeChild.getUserObject();
          Token token = astNode.getToken();

          if ((token.getLine() > line || token.getLine() == line && token.getColumn() >= column) && getStartOffset(token) < minimumOffset) {
            minimumOffset = getStartOffset(token);
            treeNode = treeNodeChild;
          }
        }
      }
      checkState(treeNode != null, "unable to find the AstNode following the caret position " + line + ":" + column);

      astTree.clearSelection();
      astTree.addSelectionPath(new TreePath(treeNode.getPath()));

      highlightSelectedPaths();
    }
  }

  private void scrollToSelectedPath() {
    TreePath selectionPath = astTree.getSelectionPath();
    if (selectionPath != null) {
      astTree.scrollPathToVisible(selectionPath);
    }
  }

  private void loadFromFile(File file) {
    try {
      loadFromString(FileUtils.readFileToString(file));
    } catch (IOException e) {
      LOG.error("Unable to load the code file '" + file.getAbsolutePath() + "'", e);
    }
  }

  private void loadFromString(String code) {
    computeLineOffsets(code);

    showCode(code);
    showAst(code);
  }

  private void computeLineOffsets(String code) {
    lineOffsets.clear();

    int currentOffset = 1;

    String[] lines = code.split("(\r)?\n", -1);
    for (int line = 1; line <= lines.length; line++) {
      lineOffsets.put(line, currentOffset);
      currentOffset += lines[line - 1].length() + 1;
    }
  }

  private int getLineFromOffset(int offset) {
    int line;

    for (line = 1; lineOffsets.containsKey(line + 1) && offset >= lineOffsets.get(line + 1); line++) {
    }

    return line;
  }

  private int getColumnFromOffsetAndLine(int offset, int line) {
    return offset - lineOffsets.get(line);
  }

  private int getStartOffset(Token token) {
    return getOffset(token.getLine(), token.getColumn());
  }

  private int getEndOffset(Token token) {
    String[] tokenLines = token.getOriginalValue().split("(\r)?\n", -1);

    int tokenLastLine = token.getLine() + tokenLines.length - 1;
    int tokenLastLineColumn = (tokenLines.length > 1 ? 0 : token.getColumn()) + tokenLines[tokenLines.length - 1].length();

    return getOffset(tokenLastLine, tokenLastLineColumn);
  }

  private int getOffset(int line, int column) {
    return lineOffsets.containsKey(line) ?
        Math.min(lineOffsets.get(line) + column, codeEditor.getDocument().getEndPosition().getOffset() - 1) :
        codeEditor.getDocument().getEndPosition().getOffset() - 1;
  }

  private void showCode(String code) {
    StringBuffer sb = new StringBuffer();
    sb.append("<html><head><style type=\"text/css\">");
    sb.append(getCss());
    sb.append("</style></head><body><pre class=\"code\">");
    sb.append(htmlRenderer.render(new StringReader(code), colorizerTokenizers));
    sb.append("</pre></body></html>");

    codeEditor.setText(sb.toString());
    codeEditor.setCaretPosition(0);
  }

  private void showAst(String code) {
    if (!EMPTY_TREE_MODEL.equals(astTree.getModel())) {
      astTree.setModel(EMPTY_TREE_MODEL);
      userObjectToTreeNodeCache.clear();
    }

    if (!code.isEmpty()) {
      try {
        AstNode astNode = parser.parse(code);
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(astNode);
        userObjectToTreeNodeCache.put(astNode, treeNode);

        addChildNodes(treeNode, astNode);

        astTree.setModel(new DefaultTreeModel(treeNode));
      } catch (RecognitionException re) {
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

  private Object getCss() {
    try {
      InputStream inputStream = SsdkGui.class.getResourceAsStream(CSS_PATH);
      if (inputStream == null) {
        throw new FileNotFoundException("Unable to find the resource " + CSS_PATH);
      }
      return IOUtils.toString(inputStream);
    } catch (IOException e) {
      LOG.error("Unable to read the CSS file '" + CSS_PATH + "'", e);
      return "";
    }
  }

}
