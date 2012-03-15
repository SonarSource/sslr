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

    assertThat(expr.getValue(tree), is(leaf));
  }

  @Test
  public void getValuesTest() {
    AstNodeXPathQuery<AstNode> expr = AstNodeXPathQuery.create("//leaf");
    AstNode tree = new AstNode(new NodeType(), "tree", null);
    AstNode branch = new AstNode(new NodeType(), "branch", null);
    AstNode leaf1 = new AstNode(new NodeType(), "leaf", null);
    AstNode leaf2 = new AstNode(new NodeType(), "leaf", null);
    tree.addChild(branch);
    branch.addChild(leaf1);
    branch.addChild(leaf2);

    assertThat(expr.getValues(tree).size(), is(2));
  }

  static class NodeType implements AstNodeType {

  }

}
