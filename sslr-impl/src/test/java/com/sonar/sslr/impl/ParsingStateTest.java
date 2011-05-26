/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class ParsingStateTest {

  private ParsingState state;

  @Before
  public void init() {
    List<Token> tokens = new ArrayList<Token>();
    tokens.add(new Token(MockTokenType.WORD, "java"));
    tokens.add(new Token(MockTokenType.WORD, "public"));
    tokens.add(new Token(MockTokenType.WORD, "class"));

    state = new ParsingState(tokens);
  }

  @Test
  public void testPopToken() {
    assertEquals("java", state.popToken(new RuleMatcher("Dummy")).getValue());
    assertEquals("public", state.popToken(new RuleMatcher("Dummy")).getValue());
  }

  @Test
  public void testPeekToken() {
    assertEquals("java", state.peekToken(new RuleMatcher("Dummy")).getValue());
    assertEquals("java", state.peekToken(new RuleMatcher("Dummy")).getValue());
  }

  @Test
  public void testGetIndex() {
    assertEquals(0, state.lexerIndex);
    state.popToken(new RuleMatcher("Dummy")).getValue();
    assertEquals(1, state.lexerIndex);
    state.peekToken(new RuleMatcher("Dummy")).getValue();
    assertEquals(1, state.lexerIndex);
  }

  @Test
  public void testHasNextToken() {
    assertTrue(state.hasNextToken());
    state.popToken(new RuleMatcher("Dummy")).getValue();
    state.popToken(new RuleMatcher("Dummy")).getValue();
    state.popToken(new RuleMatcher("Dummy")).getValue();
    assertFalse(state.hasNextToken());
  }

  @Test
  public void testGetOutpostMatcherOnPeek() {
    state.popToken(new RuleMatcher("Dummy1"));
    state.peekToken(new RuleMatcher("Dummy2"));
    assertEquals("public", state.getOutpostMatcherToken().getValue());
  }

  @Test
  public void testGetOutpostMatcherOnPop() {
    state.popToken(new RuleMatcher("Dummy1"));
    state.popToken(new RuleMatcher("Dummy2"));
    assertEquals("public", state.getOutpostMatcherToken().getValue());
  }

  @Test
  public void testHasMemoizedAst() {
    assertFalse(state.hasMemoizedAst(new RuleMatcher("Dummy")));

    state.popToken(new RuleMatcher("Dummy"));
    RuleMatcher myrule = new RuleMatcher("MyRule");
    AstNode astNode = new AstNode(myrule, "MyRule", null);
    astNode.setFromIndex(1);
    assertEquals(0, astNode.getToIndex());

    state.memoizeAst(myrule, astNode);

    assertEquals(1, astNode.getToIndex());
    state.popToken(new RuleMatcher("Dummy"));
    assertFalse(state.hasMemoizedAst(myrule));
    assertNull(state.getMemoizedAst(myrule));
  }

  @Test
  public void testGetMemoizedAst() {
    RuleMatcher myrule = new RuleMatcher("MyRule");
    state.popToken(myrule);
    state.popToken(myrule);
    AstNode astNode = new AstNode(myrule, "MyRule", null);
    astNode.setFromIndex(0);
    state.memoizeAst(myrule, astNode);

    state.lexerIndex = 0;
    assertTrue(state.hasMemoizedAst(myrule));
    assertNotNull(state.getMemoizedAst(myrule));

    assertEquals(2, state.lexerIndex);
  }
}
