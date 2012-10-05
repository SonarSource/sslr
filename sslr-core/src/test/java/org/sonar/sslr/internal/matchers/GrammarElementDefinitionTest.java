/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.internal.matchers;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GrammarElementDefinitionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Matcher subMatcher;
  private MatcherContext context, subContext;
  private GrammarElementMatcher matcher;

  @Before
  public void setUp() {
    subMatcher = mock(Matcher.class);
    subContext = mock(MatcherContext.class);
    context = mock(MatcherContext.class);
    when(context.getSubContext(subMatcher)).thenReturn(subContext);
    matcher = new GrammarElementMatcher("foo");
  }

  /**
   * SSLR-214
   */
  @Test
  public void redefinition() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("'foo' has been already defined\nat " + expectedStackTraceElement(new Throwable().getStackTrace()[0]));
    matcher.is(subMatcher);
    matcher.is(subMatcher);
  }

  @Test
  public void should_override() {
    matcher.is(subMatcher);
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("'foo' has been already defined\nat " + expectedStackTraceElement(new Throwable().getStackTrace()[0]));
    matcher.override(subMatcher);
    matcher.is(subMatcher);
  }

  @Test
  public void should_mock() {
    matcher.is(subMatcher);
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("'foo' has been already defined\nat " + expectedStackTraceElement(new Throwable().getStackTrace()[0]));
    matcher.mock();
    matcher.is(subMatcher);
  }

  private static StackTraceElement expectedStackTraceElement(StackTraceElement stackTraceElement) {
    return new StackTraceElement(
        stackTraceElement.getClassName(),
        stackTraceElement.getMethodName(),
        stackTraceElement.getFileName(),
        stackTraceElement.getLineNumber() + 1);
  }

  @Test
  public void should_match() {
    matcher.is(subMatcher);
    when(subContext.runMatcher()).thenReturn(true);
    assertThat(matcher.match(context)).isTrue();
    verify(subContext).runMatcher();
  }

  @Test
  public void should_not_match() {
    matcher.is(subMatcher);
    when(subContext.runMatcher()).thenReturn(false);
    assertThat(matcher.match(context)).isFalse();
    verify(subContext).runMatcher();
  }

  @Test
  public void recoveryRule_unsupported() {
    thrown.expect(UnsupportedOperationException.class);
    matcher.recoveryRule();
  }

  @Test
  public void test_skip() {
    matcher.skip();
    AstNode astNode = mock(AstNode.class);
    assertThat(matcher.hasToBeSkippedFromAst(astNode)).isTrue();
  }

  @Test
  public void test_skipIfOneChild() {
    matcher.skipIfOneChild();
    AstNode astNode = mock(AstNode.class);
    when(astNode.getNumberOfChildren()).thenReturn(1, 2);
    assertThat(matcher.hasToBeSkippedFromAst(astNode)).isTrue();
    assertThat(matcher.hasToBeSkippedFromAst(astNode)).isFalse();
  }

  @Test
  public void test_skipIf() {
    AstNodeSkippingPolicy policy = mock(AstNodeSkippingPolicy.class);
    AstNode astNode = mock(AstNode.class);
    matcher.skipIf(policy);
    assertThat(matcher.hasToBeSkippedFromAst(astNode)).isFalse();
    verify(policy).hasToBeSkippedFromAst(astNode);
  }

}
