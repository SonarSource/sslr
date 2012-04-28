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
package com.sonar.sslr.impl;

import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class ParsingStateTest {

  private ParsingState state;

  @Before
  public void init() {
    List<Token> tokens = new ArrayList<Token>();
    tokens.add(mockToken(MockTokenType.WORD, "java"));
    tokens.add(mockToken(MockTokenType.WORD, "public"));
    tokens.add(mockToken(MockTokenType.WORD, "class"));

    state = new ParsingState(tokens);
  }

  @Test
  public void testPopToken() {
    assertEquals("java", state.popToken(getRuleMatcher("Dummy")).getValue());
    assertEquals("public", state.popToken(getRuleMatcher("Dummy")).getValue());
  }

  @Test
  public void testPeekToken() {
    assertEquals("java", state.peekToken(getRuleMatcher("Dummy")).getValue());
    assertEquals("java", state.peekToken(getRuleMatcher("Dummy")).getValue());
  }

  @Test
  public void testGetIndex() {
    assertEquals(0, state.lexerIndex);
    state.popToken(getRuleMatcher("Dummy")).getValue();
    assertEquals(1, state.lexerIndex);
    state.peekToken(getRuleMatcher("Dummy")).getValue();
    assertEquals(1, state.lexerIndex);
  }

  @Test
  public void testHasNextToken() {
    assertTrue(state.hasNextToken());
    state.popToken(getRuleMatcher("Dummy")).getValue();
    state.popToken(getRuleMatcher("Dummy")).getValue();
    state.popToken(getRuleMatcher("Dummy")).getValue();
    assertFalse(state.hasNextToken());
  }

  @Test
  public void testGetOutpostMatcherOnPeek() {
    state.popToken(getRuleMatcher("Dummy1"));
    state.peekToken(getRuleMatcher("Dummy2"));
    assertEquals("public", state.getOutpostMatcherToken().getValue());
  }

  @Test
  public void testGetOutpostMatcherOnPop() {
    state.popToken(getRuleMatcher("Dummy1"));
    state.popToken(getRuleMatcher("Dummy2"));
    assertEquals("public", state.getOutpostMatcherToken().getValue());
  }

  @Test
  public void testHasMemoizedAst() {
    assertFalse(state.hasMemoizedAst(getRuleMatcher("Dummy")));

    state.popToken(getRuleMatcher("Dummy"));
    RuleMatcher myrule = getRuleMatcher("MyRule");
    AstNode astNode = new AstNode(RuleDefinition.newRuleBuilder(myrule), "MyRule", null);
    astNode.setFromIndex(1);
    assertEquals(0, astNode.getToIndex());

    state.memoizeAst(myrule, astNode);

    assertEquals(1, astNode.getToIndex());
    state.popToken(getRuleMatcher("Dummy"));
    assertFalse(state.hasMemoizedAst(myrule));
    assertNull(state.getMemoizedAst(myrule));
  }

  @Test
  public void testGetMemoizedAst() {
    RuleMatcher myrule = getRuleMatcher("MyRule");
    state.popToken(myrule);
    state.popToken(myrule);
    AstNode astNode = new AstNode(RuleDefinition.newRuleBuilder(myrule), "MyRule", null);
    astNode.setFromIndex(0);
    state.memoizeAst(myrule, astNode);

    state.lexerIndex = 0;
    assertTrue(state.hasMemoizedAst(myrule));
    assertNotNull(state.getMemoizedAst(myrule));

    assertEquals(0, state.lexerIndex);
  }

  private static RuleMatcher getRuleMatcher(String ruleName) {
    return RuleDefinition.newRuleBuilder(ruleName).getRule();
  }
}
