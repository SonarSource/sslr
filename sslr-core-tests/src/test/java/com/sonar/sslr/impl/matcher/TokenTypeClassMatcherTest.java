/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
    assertThat(and(GenericTokenType.class), match("word"));
    assertThat(and(MockTokenType.class), not(match("word")));
  }

  @Test
  public void testToString() {
    assertEquals(and(GenericTokenType.class).toString(), GenericTokenType.class.getCanonicalName() + ".class");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(and(GenericTokenType.class) == and(GenericTokenType.class), is(true));
    assertThat(and(GenericTokenType.class) == and(MockTokenType.class), is(false));
    assertThat(and(GenericTokenType.class) == adjacent("("), is(false));
  }

}
