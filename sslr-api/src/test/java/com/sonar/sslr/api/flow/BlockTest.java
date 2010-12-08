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

import java.util.Iterator;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;

public class BlockTest {

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenTryingToIterateWithoutLastInstruction() {
    Block block = new Block(createAstNode("instruction"));
    block.iterator();
  }

  @Test
  public void shouldIterateOverInstructions() {
    AstNode parent = createAstNode("parent");
    AstNode instruction1 = createAstNode("instruction");
    AstNode instruction2 = createAstNode("instruction");
    AstNode instruction3 = createAstNode("instruction");
    parent.addChild(instruction1);
    parent.addChild(instruction2);
    parent.addChild(instruction3);

    Block block = new Block(instruction1);
    block.setLastInstruction(instruction3);

    Iterator<AstNode> instructions = block.iterator();
    assertThat(instructions.hasNext(), is(true));
    assertThat(instructions.next(), is(instruction1));
    assertThat(instructions.next(), is(instruction2));
    assertThat(instructions.next(), is(instruction3));
    assertThat(instructions.hasNext(), is(false));
  }

  @Test
  public void shouldBeEqualsWhenStartingWithTheSameInstruction() {
    AstNode instruction = createAstNode("instruction");
    assertThat(new Block(instruction), equalTo(new Block(instruction)));

    AstNode anotherInstruction = createAstNode("instruction");
    assertThat(new Block(anotherInstruction), not(equalTo(new Block(instruction))));
  }

  @Test
  public void shouldHaveTheSameHashcodeWhenStartingWithTheSameInstruction() {
    AstNode instruction = createAstNode("instruction");
    assertThat(new Block(instruction).hashCode(), is(new Block(instruction).hashCode()));

    AstNode anotherInstruction = createAstNode("instruction");
    assertThat(new Block(anotherInstruction).hashCode(), not(is(new Block(instruction).hashCode())));
  }

}
