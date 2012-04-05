/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class StatelessMatcherTest {

  @Test
  public void testEqualsAndHashCode() {
    Matcher child1 = mock(Matcher.class);
    Matcher child2 = mock(Matcher.class);
    StatelessMatcher matcher1 = newMatcher(child1);
    StatelessMatcher matcher2 = newMatcher(child1);
    StatelessMatcher matcher3 = newMatcher(child2);

    assertThat(matcher1.equals(matcher1), is(true));
    assertThat(matcher1.equals(matcher2), is(true));
    assertThat(matcher1.hashCode() == matcher2.hashCode(), is(true));

    assertThat(matcher1.equals(null), is(false));
    assertThat(matcher1.equals(new Object()), is(false));
    assertThat(matcher1.equals(matcher3), is(false));
  }

  private StatelessMatcher newMatcher(Matcher child) {
    return new StatelessMatcher(child) {
      @Override
      protected AstNode matchWorker(ParsingState parsingState) {
        return null;
      }
    };
  }

}
