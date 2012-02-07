/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.ast;

import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;

public class SkipFromAstIfOnlyOneChildTest {

  @Test
  public void testHasToBeSkippedFromAst() {
    AstNode parent = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "identifier"));
    AstNode child1 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child1"));
    AstNode child2 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child2"));
    parent.addChild(child1);
    parent.addChild(child2);
    child2.addChild(child1);

    SkipFromAstIfOnlyOneChild astNodeType = new SkipFromAstIfOnlyOneChild();

    assertThat(astNodeType.hasToBeSkippedFromAst(parent), is(false));
    assertThat(astNodeType.hasToBeSkippedFromAst(child1), is(false));
    assertThat(astNodeType.hasToBeSkippedFromAst(child2), is(true));
  }

}
