/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static com.sonar.sslr.api.AstNodeUtils.createAstNode;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;

public class BlockTest {

  @Test
  public void shouldBeEqualsWhenStartingWithTheSameInstruction() {
    AstNode stmt = createAstNode("stmt");
    assertThat(new Block(stmt), equalTo(new Block(stmt)));

    AstNode anotherStmt = createAstNode("stmt");
    assertThat(new Block(anotherStmt), not(equalTo(new Block(stmt))));
  }

  @Test
  public void shouldHaveTheSameHashcodeWhenStartingWithTheSameInstruction() {
    AstNode stmt = createAstNode("stmt");
    assertThat(new Block(stmt).hashCode(), is(new Block(stmt).hashCode()));

    AstNode anotherStmt = createAstNode("stmt");
    assertThat(new Block(anotherStmt).hashCode(), not(is(new Block(stmt).hashCode())));
  }

  @Test
  public void shouldGetLastStatement() {
    Block block = new Block(createAstNode("stmt1"));
    block.addStatement(createAstNode("stmt2"));
    AstNode lastStmt = createAstNode("lastStmt");
    block.addStatement(lastStmt);

    assertThat(block.getLastStatement(), is(lastStmt));
  }

  @Test
  public void shouldGetFirstStatement() {
    AstNode firstStmt = createAstNode("firstStmt");
    Block block = new Block(firstStmt);
    block.addStatement(createAstNode("stmt2"));

    assertThat(block.getFirstStatement(), is(firstStmt));
  }

}
