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

import org.sonar.sslr.matchers.ParseRunner;

import org.junit.Before;
import org.junit.Test;
import org.sonar.sslr.matchers.ParsingResult;

import static org.fest.assertions.Assertions.assertThat;

public class ExpressionGrammarTest {

  private ExpressionGrammar grammar;

  @Before
  public void setUp() {
    grammar = new ExpressionGrammar();
  }

  @Test
  public void match() {
    String inputString = "20 * ( 2 + 2 ) - var";
    char[] input = inputString.toCharArray();
    MatcherContext matcherContext = new BasicMatcherContext(input, (Matcher) grammar.root);
    assertThat(matcherContext.runMatcher()).isTrue();
    ParseTreePrinter.print(matcherContext.getNode(), input);
    assertThat(matcherContext.getCurrentIndex()).isEqualTo(input.length);
    assertThat(ParseTreePrinter.leafsToString(matcherContext.getNode(), input)).as("full-fidelity").isEqualTo(inputString);
  }

  @Test
  public void mismatch() {
    String inputString = "term +";
    char[] input = inputString.toCharArray();
    ParseRunner parseRunner = new ParseRunner(grammar.expression);
    ParsingResult result = parseRunner.parse(input);
    assertThat(result.isMatched()).isFalse();
  }

  @Test
  public void should_mock() {
    String inputString = "term + term";
    char[] input = inputString.toCharArray();
    grammar.term.mock();
    MatcherContext matcherContext = new BasicMatcherContext(input, (Matcher) grammar.expression);
    assertThat(matcherContext.runMatcher()).isTrue();
    ParseTreePrinter.print(matcherContext.getNode(), input);
    assertThat(matcherContext.getCurrentIndex()).isEqualTo(input.length);
    assertThat(ParseTreePrinter.leafsToString(matcherContext.getNode(), input)).as("full-fidelity").isEqualTo(inputString);
  }

}
