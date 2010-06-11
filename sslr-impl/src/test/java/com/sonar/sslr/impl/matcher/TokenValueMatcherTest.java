/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.test.lexer.TokenUtils.split;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class TokenValueMatcherTest {

  @Test
  public void testMatch() {
    TokenValueMatcher matcher = new TokenValueMatcher("print");
    AstNode node = matcher.match(new ParsingState(split("print screen")));

    assertEquals("print", node.getTokenValue());
  }

  @Test
  public void testToString() {
    TokenValueMatcher matcher = new TokenValueMatcher("print");
    assertEquals("print", matcher.toString());
  }

}
