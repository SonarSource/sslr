/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TokenCountMatcherTest {

  @Test
  public void ok() {
    assertThat(and(tokenCount(TokenCountMatcher.Operator.EQUAL, 2, till("b"))), match("a b"));
    assertThat(and(tokenCount(TokenCountMatcher.Operator.EQUAL, 3, till("b"))), match("a a b"));
    assertThat(and(tokenCount(TokenCountMatcher.Operator.LESS_THAN, 5, till("b"))), match("a b"));
    assertThat(and(tokenCount(TokenCountMatcher.Operator.GREATER_THAN, 1, till("b"))), match("a b"));
    
    assertThat(and(tokenCount(TokenCountMatcher.Operator.EQUAL, 0, till("b"))), not(match("a b")));
    assertThat(and(tokenCount(TokenCountMatcher.Operator.EQUAL, 4, till("b"))), not(match("a a b")));
    assertThat(and(tokenCount(TokenCountMatcher.Operator.LESS_THAN, 2, till("b"))), not(match("a b")));
    assertThat(and(tokenCount(TokenCountMatcher.Operator.GREATER_THAN, 5, till("b"))), not(match("a a a a b")));
  }

  @Test
  public void testToString() {
    assertEquals(tokenCount(TokenCountMatcher.Operator.EQUAL, 2, till("b")).toString(), "tokenCount(TokenCountMatcher.Operator.EQUAL, 2)");
  }

}
