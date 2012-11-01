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
package org.sonar.sslr.parser;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;
import org.sonar.sslr.internal.matchers.MatcherContext;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParseRunnerTest {

  @org.junit.Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_not_accept_null() {
    thrown.expect(NullPointerException.class);
    new ParseRunner(null);
  }

  @Test
  public void should_report_error_at_rule_level() {
    Rule rule = new GrammarElementMatcher("rule").is("foo", "bar");
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("foo".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(3);
    assertThat(parseError.getMessage()).isEqualTo("failed to match: rule");
    assertThat(parseError.getFailedPaths()).hasSize(1);
  }

  @Test
  public void should_report_error_at_end_of_input() {
    Rule endOfInput = new GrammarElementMatcher("endOfInput").is(GrammarOperators.endOfInput());
    Rule rule = new GrammarElementMatcher("rule").is("foo", endOfInput);
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("foo bar".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(3);
    assertThat(parseError.getMessage()).isEqualTo("failed to match: endOfInput");
    assertThat(parseError.getFailedPaths()).hasSize(1);
  }

  @Test
  public void should_not_report_error_inside_of_predicate_not() {
    Rule subRule = new GrammarElementMatcher("subRule").is("foo");
    Rule rule = new GrammarElementMatcher("rule").is(GrammarOperators.nextNot(subRule), "bar");
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("baz".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
    assertThat(parseError.getMessage()).isEqualTo("failed to match: rule");
    assertThat(parseError.getFailedPaths()).hasSize(1);
  }

  @Test
  public void should_report_error_inside_of_predicate_next() {
    Rule subRule = new GrammarElementMatcher("subRule").is("foo");
    Rule rule = new GrammarElementMatcher("rule").is(GrammarOperators.next(subRule), "bar");
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("baz".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
    assertThat(parseError.getMessage()).isEqualTo("failed to match: subRule");
    assertThat(parseError.getFailedPaths()).hasSize(1);
  }

  @Test
  public void should_not_report_error_inside_of_token() {
    Rule subRule = new GrammarElementMatcher("subRule").is("foo");
    Rule rule = new GrammarElementMatcher("rule").is(GrammarOperators.token(GenericTokenType.IDENTIFIER, subRule), "bar");
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("baz".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
    assertThat(parseError.getMessage()).isEqualTo("failed to match: rule");
    assertThat(parseError.getFailedPaths()).hasSize(1);
  }

  @Test
  public void should_report_error_at_several_paths() {
    Rule subRule1 = new GrammarElementMatcher("subRule1").is("foo");
    Rule subRule2 = new GrammarElementMatcher("subRule2").is("bar");
    Rule rule = new GrammarElementMatcher("rule").is(GrammarOperators.firstOf(subRule1, subRule2));
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("baz".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
    assertThat(parseError.getMessage()).isEqualTo("failed to match none of: subRule1 subRule2");
    assertThat(parseError.getFailedPaths()).hasSize(2);
  }

  @Test
  public void should_fail_if_result_of_parse_depends_not_only_from_input() {
    GrammarElementMatcher rule = mock(GrammarElementMatcher.class);
    when(rule.match(Mockito.any(MatcherContext.class))).thenReturn(false).thenReturn(true);
    thrown.expect(IllegalStateException.class);
    new ParseRunner(rule).parse("baz".toCharArray());
  }

}
