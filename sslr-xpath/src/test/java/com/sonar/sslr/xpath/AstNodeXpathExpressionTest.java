package com.sonar.sslr.xpath;

/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

import static junit.framework.Assert.assertEquals;

public class AstNodeXpathExpressionTest {

  @Test
  public void testSearch() {
    AstNodeXpathExpression expr = new AstNodeXpathExpression("branch/leaf");
    AstNode tree = new AstNode(new NodeType(), "tree", null);
    AstNode branch = new AstNode(new NodeType(), "branch", null);
    AstNode leaf = new AstNode(new NodeType(), "leaf", null);
    tree.addChild(branch);
    branch.addChild(leaf);
    AstNode result = expr.search(tree);
    assertEquals(leaf, result);
  }

  static class NodeType implements AstNodeType {

  }

}
