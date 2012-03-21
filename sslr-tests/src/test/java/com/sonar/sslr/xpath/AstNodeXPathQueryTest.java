/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class AstNodeXPathQueryTest {

  @Test
  public void getValueTest() {
    AstNodeXPathQuery<AstNode> expr = AstNodeXPathQuery.create("tree/branch/leaf");
    AstNode tree = new AstNode(new NodeType(), "tree", null);
    AstNode branch = new AstNode(new NodeType(), "branch", null);
    AstNode leaf = new AstNode(new NodeType(), "leaf", null);
    tree.addChild(branch);
    branch.addChild(leaf);

    assertThat(expr.selectSingleNode(tree), is(leaf));
  }

  @Test
  public void selectSingleNodeNoResultTest() {
    AstNodeXPathQuery<AstNode> expr = AstNodeXPathQuery.create("tree/branch");
    AstNode tree = new AstNode(new NodeType(), "tree", null);

    assertThat(expr.selectSingleNode(tree), nullValue());
  }

  @Test
  public void selectNodesTest() {
    AstNodeXPathQuery<AstNode> expr = AstNodeXPathQuery.create("//leaf");
    AstNode tree = new AstNode(new NodeType(), "tree", null);
    AstNode branch = new AstNode(new NodeType(), "branch", null);
    AstNode leaf1 = new AstNode(new NodeType(), "leaf", null);
    AstNode leaf2 = new AstNode(new NodeType(), "leaf", null);
    tree.addChild(branch);
    branch.addChild(leaf1);
    branch.addChild(leaf2);

    assertThat(expr.selectNodes(tree).size(), is(2));
  }

  @Test
  public void selectNodesNoResultTest() {
    AstNodeXPathQuery<AstNode> expr = AstNodeXPathQuery.create("//branch");
    AstNode tree = new AstNode(new NodeType(), "tree", null);

    assertThat(expr.selectNodes(tree).size(), is(0));
  }

  static class NodeType implements AstNodeType {

  }

}
