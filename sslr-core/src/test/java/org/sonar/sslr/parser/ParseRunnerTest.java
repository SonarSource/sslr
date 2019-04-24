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
package org.sonar.sslr.parser;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.internal.grammar.MutableParsingRule;

import static org.fest.assertions.Assertions.assertThat;

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
    Rule rule = new MutableParsingRule("rule").is("foo", "bar");
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("foo".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(3);
  }

  @Test
  public void should_report_error_at_end_of_input() {
    Rule endOfInput = new MutableParsingRule("endOfInput").is(GrammarOperators.endOfInput());
    Rule rule = new MutableParsingRule("rule").is("foo", endOfInput);
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("foo bar".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(3);
  }

  @Test
  public void should_not_report_error_inside_of_predicate_not() {
    Rule subRule = new MutableParsingRule("subRule").is("foo");
    Rule rule = new MutableParsingRule("rule").is(GrammarOperators.nextNot(subRule), "bar");
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("baz".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
  }

  @Test
  public void should_report_error_at_correct_index() {
    Rule rule = new MutableParsingRule("rule").is(GrammarOperators.nextNot("foo"));
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("foo".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
  }

  @Test
  public void should_report_error_inside_of_predicate_next() {
    Rule subRule = new MutableParsingRule("subRule").is("foo");
    Rule rule = new MutableParsingRule("rule").is(GrammarOperators.next(subRule), "bar");
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("baz".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
  }

  @Test
  public void should_not_report_error_inside_of_token() {
    Rule subRule = new MutableParsingRule("subRule").is("foo");
    Rule rule = new MutableParsingRule("rule").is(GrammarOperators.token(GenericTokenType.IDENTIFIER, subRule), "bar");
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("baz".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
  }

  @Test
  public void should_not_report_error_inside_of_trivia() {
    Rule subRule = new MutableParsingRule("subRule").is("foo");
    Rule rule = new MutableParsingRule("rule").is(GrammarOperators.skippedTrivia(subRule), "bar");
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("baz".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
  }

  @Test
  public void should_report_error_at_several_paths() {
    Rule subRule1 = new MutableParsingRule("subRule1").is("foo");
    Rule subRule2 = new MutableParsingRule("subRule2").is("bar");
    Rule rule = new MutableParsingRule("rule").is(GrammarOperators.firstOf(subRule1, subRule2));
    ParseRunner runner = new ParseRunner(rule);
    ParsingResult result = runner.parse("baz".toCharArray());
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.println(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(0);
  }

}
