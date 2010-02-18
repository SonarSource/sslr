/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import com.sonarsource.cobol.lexer.CobolLexer;
import com.sonarsource.cobol.parser.CobolTokenType;
import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.ast.AstNode;

import static org.junit.Assert.assertEquals;

public class TokenTypeMatcherTest {

  @Test
  public void testMatch() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(CobolTokenType.WORD);
    CobolLexer lexer = new CobolLexer();
    AstNode node = matcher.match(new ParsingState(lexer.lex("print screen")));

    assertEquals(CobolTokenType.WORD, node.getTokenType());
  }

  @Test
  public void testToString() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(CobolTokenType.WORD);
    assertEquals("WORD", matcher.toString());
  }
}
