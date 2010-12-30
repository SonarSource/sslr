/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static com.sonar.sslr.api.AstNodeUtils.createAstNode;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;

public class ControlFlowGraphTest {

  ControlFlowGraph graph = new ControlFlowGraph();

  @Test(expected = IllegalStateException.class)
  public void twoBlocksShouldNotBeCreatedWithTheSameFirstStatement() {
    AstNode stmt = createAstNode("stmt");
    graph.createBlock(stmt);
    graph.createBlock(stmt);
  }

  @Test(expected = IllegalStateException.class)
  public void twoBlocksShouldNotContainTheSameStatement() {
    AstNode oneStmt = createAstNode("oneStmt");
    graph.createBlock(oneStmt);
    Block secondBlock = graph.createBlock(createAstNode("anotherStmt"));
    graph.addStatement(secondBlock, oneStmt);
  }

  @Test
  public void testGetBlockContaining() {
    AstNode stmt = createAstNode("stmt");
    Block block = graph.createBlock(stmt);

    assertThat(graph.getBlockContaining(stmt), is(block));
    assertThat(graph.oneBlockContains(stmt), is(true));
  }

  @Test
  public void oneBlockShouldContainAddedStatement() {
    Block block = graph.createBlock(createAstNode("stmt"));
    AstNode myStmt = createAstNode("myStmt");
    graph.addStatement(block, myStmt);

    assertThat(graph.getBlockContaining(myStmt), is(block));
    assertThat(graph.oneBlockContains(myStmt), is(true));
  }
}
