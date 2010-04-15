/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.TokenValueMatcher;

import static org.junit.Assert.assertEquals;

public class TokenValueMatcherTest {

  private WordLexer lexer = new WordLexer();

  @Test
  public void testMatch() {
    TokenValueMatcher matcher = new TokenValueMatcher("print");
    AstNode node = matcher.match(new ParsingState(lexer.lex("print screen")));

    assertEquals("print", node.getTokenValue());
  }

  @Test
  public void testToString() {
    TokenValueMatcher matcher = new TokenValueMatcher("print");
    assertEquals("print", matcher.toString());
  }

}
