/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class AnythingMatcherTest {

  private WordLexer lexer = new WordLexer();

  @Test
  public void testMatch() {
    AnythingMatcher matcher = new AnythingMatcher();
    AstNode node = matcher.match(new ParsingState(lexer.lex("print screen").getTokens()));
    assertEquals("print", node.getTokenValue());
    
    node = matcher.match(new ParsingState(lexer.lex(".").getTokens()));
    assertEquals(".", node.getTokenValue());
  }
}
