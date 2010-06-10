/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.MockTokenType;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.TokenTypeMatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenTypeMatcherTest {

  @Test
  public void testMatch() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(MockTokenType.WORD);
    WordLexer lexer = new WordLexer();
    AstNode node = matcher.match(new ParsingState(lexer.lex("print screen").getTokens()));

    assertTrue(node.is(MockTokenType.WORD));
  }

  @Test
  public void testToString() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(MockTokenType.WORD);
    assertEquals("WORD", matcher.toString());
  }
}
