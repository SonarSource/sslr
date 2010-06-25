/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.MockTokenType;

public class TokenTypeClassMatcherTest {

  @Test
  public void testIsExpectedToken() {
    TokenTypeClassMatcher matcher = new TokenTypeClassMatcher(GenericTokenType.class);
    assertFalse(matcher.isExpectedToken(new Token(MockTokenType.WORD2, "word2")));
    assertTrue(matcher.isExpectedToken(new Token(GenericTokenType.IDENTIFIER, "word2")));
  }

  @Test
  public void testThroughMatchers() {
    assertThat(Matchers.and(GenericTokenType.class), match("word"));
    assertThat(Matchers.and(MockTokenType.class), not(match("word")));
  }
}
