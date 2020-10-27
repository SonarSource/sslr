/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.tests;

import com.sonar.sslr.api.Rule;
import org.junit.Before;
import org.junit.Test;
import org.sonar.sslr.internal.grammar.MutableParsingRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class RuleAssertTest {

  private Rule rule;

  @Before
  public void setUp() {
    rule = new MutableParsingRule("ruleName").is("foo");
  }

  @Test
  public void ok() {
    new RuleAssert(rule)
        .matches("foo")
        .notMatches("bar");
  }

  @Test
  public void test_matches_failure() {
    ParsingResultComparisonFailure thrown = assertThrows(ParsingResultComparisonFailure.class,
      () -> new RuleAssert(rule).matches("bar"));
    assertTrue(thrown.getMessage().contains("Rule 'ruleName' should match:\nbar"));
  }

  @Test
  public void test_notMatches_failure() {
    AssertionError thrown = assertThrows(AssertionError.class,
      () -> new RuleAssert(rule).notMatches("foo"));
    assertEquals(thrown.getMessage(), "Rule 'ruleName' should not match:\nfoo");
  }

  @Test
  public void should_not_accept_null() {
    AssertionError thrown = assertThrows(AssertionError.class,
      () -> new RuleAssert((Rule) null).matches(""));
    assertEquals(thrown.getMessage(), "expecting actual value not to be null");
  }

  @Test
  public void notMatches_should_not_accept_prefix_match() {
    new RuleAssert(rule)
        .notMatches("foo bar");
  }

  @Test
  public void matchesPrefix_ok() {
    new RuleAssert(rule)
        .matchesPrefix("foo", " bar");
  }

  @Test
  public void matchesPrefix_full_mistmatch() {
    ParsingResultComparisonFailure thrown = assertThrows(ParsingResultComparisonFailure.class,
      () -> new RuleAssert(rule).matchesPrefix("bar", " baz"));
    assertTrue(thrown.getMessage().contains("Rule 'ruleName' should match:\nbar\nwhen followed by:\n baz"));
  }

  @Test
  public void matchesPrefix_wrong_prefix() {
    ParsingResultComparisonFailure thrown = assertThrows(ParsingResultComparisonFailure.class,
      () -> new RuleAssert(rule).matchesPrefix("foo bar", " baz"));
    assertEquals(thrown.getMessage(), "Rule 'ruleName' should match:\nfoo bar\nwhen followed by:\n baz\nbut matched:\nfoo");
  }

}
