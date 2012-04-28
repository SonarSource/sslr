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
