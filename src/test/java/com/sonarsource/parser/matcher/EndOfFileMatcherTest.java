/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import static com.sonarsource.parser.matcher.Matchers.eof;

import static org.junit.Assert.assertEquals;

public class EndOfFileMatcherTest extends MatcherCase {

  @Test
  public void testEof() {
    assertMatch(eof());
    assertNotMatch(eof(), "print", "screen");
  }

  @Test
  public void testToString() {
    assertEquals("EOF", eof().toString());
    assertEquals("exit EOF", eof(new TokenValueMatcher("exit")).toString());
  }
}
