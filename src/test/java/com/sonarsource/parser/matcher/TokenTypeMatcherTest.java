/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import com.sonarsource.lexer.WordLexer;
import com.sonarsource.parser.MockTokenType;
import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.ast.AstNode;

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
