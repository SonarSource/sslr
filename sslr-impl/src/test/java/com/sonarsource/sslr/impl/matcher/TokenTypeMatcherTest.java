/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl.matcher;

import org.junit.Test;

import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.impl.MockTokenType;
import com.sonarsource.sslr.impl.ParsingState;
import com.sonarsource.sslr.impl.matcher.TokenTypeMatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenTypeMatcherTest {

  @Test
  public void testMatch() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(MockTokenType.WORD);
    WordLexer lexer = new WordLexer();
    AstNode node = matcher.match(new ParsingState(lexer.lex("print screen")));

    assertTrue(node.is(MockTokenType.WORD));
  }

  @Test
  public void testToString() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(MockTokenType.WORD);
    assertEquals("WORD", matcher.toString());
  }
}
