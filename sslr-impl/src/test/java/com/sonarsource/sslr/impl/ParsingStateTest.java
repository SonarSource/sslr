/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.api.Token;
import com.sonarsource.sslr.impl.ParsingState;
import com.sonarsource.sslr.impl.matcher.RuleImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ParsingStateTest {

  private ParsingState state;

  @Before
  public void init() {
    List<Token> tokens = new ArrayList<Token>();
    tokens.add(new Token(MockTokenType.WORD, "java"));
    tokens.add(new Token(MockTokenType.WORD, "public"));
    tokens.add(new Token(MockTokenType.WORD, "class"));

    LexerOutput lexerOutput = new LexerOutput();
    lexerOutput.addAllTokens(tokens);
    state = new ParsingState(lexerOutput);
  }

  @Test
  public void testPopToken() {
    assertEquals("java", state.popToken(new RuleImpl("Dummy")).getValue());
    assertEquals("public", state.popToken(new RuleImpl("Dummy")).getValue());
  }

  @Test
  public void testPeekToken() {
    assertEquals("java", state.peekToken(new RuleImpl("Dummy")).getValue());
    assertEquals("java", state.peekToken(new RuleImpl("Dummy")).getValue());
  }

  @Test
  public void testGetIndex() {
    assertEquals(0, state.lexerIndex);
    state.popToken(new RuleImpl("Dummy")).getValue();
    assertEquals(1, state.lexerIndex);
    state.peekToken(new RuleImpl("Dummy")).getValue();
    assertEquals(1, state.lexerIndex);
  }

  @Test
  public void testHasNextToken() {
    assertTrue(state.hasNextToken());
    state.popToken(new RuleImpl("Dummy")).getValue();
    state.popToken(new RuleImpl("Dummy")).getValue();
    state.popToken(new RuleImpl("Dummy")).getValue();
    assertFalse(state.hasNextToken());
  }

  @Test
  public void testGetOutpostMatcherOnPeek() {
    state.popToken(new RuleImpl("Dummy1"));
    state.peekToken(new RuleImpl("Dummy2"));
    assertEquals("public", state.getOutpostMatcherToken().getValue());
    assertEquals("Dummy2", state.getOutpostMatcher().toString());
  }

  @Test
  public void testGetOutpostMatcherOnPop() {
    state.popToken(new RuleImpl("Dummy1"));
    state.popToken(new RuleImpl("Dummy2"));
    assertEquals("public", state.getOutpostMatcherToken().getValue());
    assertEquals("Dummy2", state.getOutpostMatcher().toString());
  }

  @Test
  public void testHasMemoizedAst() {
    assertFalse(state.hasMemoizedAst(new RuleImpl("Dummy")));

    state.popToken(new RuleImpl("Dummy"));
    RuleImpl myrule = new RuleImpl("MyRule");
    AstNode astNode = new AstNode(myrule, "MyRule", null);
    astNode.setFromIndex(1);
    assertEquals(0, astNode.getToIndex());

    state.memoizeAst(myrule, astNode);

    assertEquals(1, astNode.getToIndex());
    state.popToken(new RuleImpl("Dummy"));
    assertFalse(state.hasMemoizedAst(myrule));
    assertNull(state.getMemoizedAst(myrule));
  }

  @Test
  public void testGetMemoizedAst() {
    RuleImpl myrule = new RuleImpl("MyRule");
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
