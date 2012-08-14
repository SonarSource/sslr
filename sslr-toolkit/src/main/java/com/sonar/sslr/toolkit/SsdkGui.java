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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.colorizer.HtmlOptions;
import org.sonar.colorizer.HtmlRenderer;
import org.sonar.colorizer.Tokenizer;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

  private static final String CSS_PATH = "/com/sonar/sslr/toolkit/codeEditor.css";
  private static final Logger LOG = LoggerFactory.getLogger("Toolkit");
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

  private final Offsets lineOffsets = new Offsets();
  private final Parser<? extends Grammar> parser;
  private final List<Tokenizer> colorizerTokenizers;
  private final HtmlRenderer htmlRenderer = new HtmlRenderer(new HtmlOptions(false, null, false));

  public SsdkGui(Parser<? extends Grammar> parser, List<Tokenizer> colorizerTokenizers) {
    this.parser = parser;
    this.colorizerTokenizers = colorizerTokenizers;

    setLayout(new BorderLayout(2, 2));
    setDefaultCloseOperation(SsdkGui.EXIT_ON_CLOSE);

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
        String code = "";
        Document document = codeEditor.getDocument();
        if (document.getLength() > 0) {
          try {
            code = document.getText(1, document.getEndPosition().getOffset() - 1);
          } catch (BadLocationException e) {
            LOG.error("Error while reading code buffer", e);
          }
        }

        int caretOffset = codeEditor.getCaretPosition();
        loadFromString(code);
        codeEditor.setCaretPosition(caretOffset);
      }
    });

    buttonPanel.add(openButton);
    buttonPanel.add(parseButton);
    add(buttonPanel, BorderLayout.PAGE_END);

    astTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    astTree.addTreeSelectionListener(new TreeSelectionListener() {
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

          codeEditor.getHighlighter().addHighlight(lineOffsets.getStartOffset(firstToken), lineOffsets.getEndOffset(lastToken),
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
        codeEditor.scrollRectToVisible(codeEditor.modelToView(lineOffsets.getOffset(line, 0)));
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
        highlightSelectedPaths();
      }
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
      codeEditor.setCaretPosition(0);
    } catch (IOException e) {
      LOG.error("Unable to load the code file '" + file.getAbsolutePath() + "'", e);
    }
  }

  private void loadFromString(String code) {
    showCode(code);
    lineOffsets.computeLineOffsets(code, codeEditor.getDocument().getEndPosition().getOffset());
    showAst(code);
  }

  private void showCode(String code) {
    StringBuffer sb = new StringBuffer();
    sb.append("<html><head><style type=\"text/css\">");
    sb.append(getCss());
    sb.append("</style></head><body><pre class=\"code\">");
    sb.append(htmlRenderer.render(new StringReader(code), colorizerTokenizers));
    sb.append("</pre></body></html>");

    codeEditor.setText(sb.toString());
  }

  private void showAst(String code) {
    if (!EMPTY_TREE_MODEL.equals(astTree.getModel())) {
      astTree.setModel(EMPTY_TREE_MODEL);
      userObjectToTreeNodeCache.clear();
    }

    if (code.length() > 0) {
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

  private void readObject(ObjectInputStream os) throws NotSerializableException {
    throw new NotSerializableException(getClass().getName());
  }

  private void writeObject(ObjectOutputStream os) throws NotSerializableException {
    throw new NotSerializableException(getClass().getName());
  }

}
