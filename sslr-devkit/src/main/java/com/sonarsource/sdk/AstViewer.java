/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sdk;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;

public class AstViewer {

  private final DefaultMutableTreeNode treeNode;
  private final DefaultTreeModel treeModel;
  private final JTree jTree;
  private final Parser parser;

  public AstViewer(Parser parser) {
    this.parser = parser;
    treeNode = new DefaultMutableTreeNode("");
    treeModel = new DefaultTreeModel(treeNode);
    jTree = new JTree(treeModel);
  }

  public JTree getJTree() {
    return jTree;
  }

  public void loadAndParse(File file) {
    treeNode.removeAllChildren();
    AstNode ast = parser.parse(file);
    treeNode.setUserObject(ast);
    updateTreeNode(treeNode, ast);
    treeModel.reload();
  }

  private void updateTreeNode(DefaultMutableTreeNode treeNode, AstNode astNode) {
    if (astNode.hasChildren()) {
      for (AstNode astNodeChild : astNode.getChildren()) {
        DefaultMutableTreeNode treeNodeChild = new DefaultMutableTreeNode(astNodeChild);
        treeNode.add(treeNodeChild);
        updateTreeNode(treeNodeChild, astNodeChild);
      }
    }
  }
}
