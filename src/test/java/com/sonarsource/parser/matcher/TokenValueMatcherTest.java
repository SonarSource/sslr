/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import com.sonarsource.cobol.lexer.CobolLexer;
import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.ast.AstNode;

import static org.junit.Assert.assertEquals;

public class TokenValueMatcherTest {

  private CobolLexer lexer = new CobolLexer();

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
