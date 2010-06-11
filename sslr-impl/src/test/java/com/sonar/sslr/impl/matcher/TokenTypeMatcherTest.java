/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.test.lexer.TokenUtils.split;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.MockTokenType;
import com.sonar.sslr.impl.ParsingState;

public class TokenTypeMatcherTest {

  @Test
  public void testMatch() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(GenericTokenType.IDENTIFIER);
    AstNode node = matcher.match(new ParsingState(split("print screen")));

    assertTrue(node.is(GenericTokenType.IDENTIFIER));
  }

  @Test
  public void testToString() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(MockTokenType.WORD);
    assertEquals("WORD", matcher.toString());
  }
}
