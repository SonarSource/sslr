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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GrammarElementMatcherTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Matcher subMatcher;
  private MatcherContext context, subContext;
  private MatchHandler matchHandler;
  private GrammarElementMatcher matcher;

  @Before
  public void setUp() {
    subMatcher = mock(Matcher.class);
    subContext = mock(MatcherContext.class);
    context = mock(MatcherContext.class);
    matchHandler = mock(MatchHandler.class);
    when(context.getMatchHandler()).thenReturn(matchHandler);
    when(context.getSubContext(subMatcher)).thenReturn(subContext);
    matcher = new GrammarElementMatcher("foo");
  }

  @Test
  public void redefinition() {
    matcher.is(subMatcher);
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule 'foo' has already been defined somewhere in the grammar.");
    matcher.is(subMatcher);
  }

  @Test
  public void should_override() {
    matcher.is(subMatcher);
    matcher.override(subMatcher);
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule 'foo' has already been defined somewhere in the grammar.");
    matcher.is(subMatcher);
  }

  @Test
  public void should_mock() {
    matcher.is(subMatcher);
    matcher.mock();
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule 'foo' has already been defined somewhere in the grammar.");
    matcher.is(subMatcher);
  }

  @Test
  public void should_match() {
    matcher.is(subMatcher);
    when(subContext.runMatcher()).thenReturn(true);
    assertThat(matcher.match(context)).isTrue();
    verify(context).createNode();
    verify(subContext).runMatcher();
    verify(matchHandler).match(context);
    verify(matchHandler).onMatch(context);
    verify(matchHandler, never()).onMissmatch(context);
  }

  @Test
  public void should_not_match() {
    matcher.is(subMatcher);
    when(subContext.runMatcher()).thenReturn(false);
    assertThat(matcher.match(context)).isFalse();
    verify(context, never()).createNode();
    verify(subContext).runMatcher();
    verify(matchHandler).match(context);
    verify(matchHandler).onMissmatch(context);
    verify(matchHandler, never()).onMatch(context);
  }

  @Test
  public void should_match_because_of_handler() {
    matcher.is(subMatcher);
    when(matchHandler.match(context)).thenReturn(true);
    assertThat(matcher.match(context)).isTrue();
    verify(context, never()).createNode();
    verify(subContext, never()).runMatcher();
    verify(matchHandler, never()).onMissmatch(context);
    verify(matchHandler, never()).onMatch(context);
  }

  @Test
  public void undefined() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule 'foo' hasn't beed defined.");
    matcher.match(context);
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
