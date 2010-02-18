/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import com.sonarsource.lexer.WordLexer;
import com.sonarsource.parser.MockTokenType;
import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.ast.AstNode;

import static org.junit.Assert.assertEquals;

public class TokenTypeMatcherTest {

  @Test
  public void testMatch() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(MockTokenType.WORD);
    WordLexer lexer = new WordLexer();
    AstNode node = matcher.match(new ParsingState(lexer.lex("print screen")));

    assertEquals(MockTokenType.WORD, node.getTokenType());
  }

  @Test
  public void testToString() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(MockTokenType.WORD);
    assertEquals("WORD", matcher.toString());
  }
}
