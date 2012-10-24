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
package org.sonar.sslr.matchers;

import org.junit.Test;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;
import org.sonar.sslr.internal.matchers.InputBuffer;
import org.sonar.sslr.internal.matchers.MatcherPathElement;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class ParseErrorFormatterTest {

  @Test
  public void test() {
    InputBuffer inputBuffer = new InputBuffer("foo\nbar\r\nbaz".toCharArray());
    ParseErrorFormatter formatter = new ParseErrorFormatter();
    GrammarElementMatcher rule1 = new GrammarElementMatcher("rule1");
    GrammarElementMatcher rule2 = new GrammarElementMatcher("rule2");
    GrammarElementMatcher rule3 = new GrammarElementMatcher("rule3");
    GrammarElementMatcher root = new GrammarElementMatcher("root");
    List<List<MatcherPathElement>> failedPaths = Arrays.asList(
        Arrays.asList(new MatcherPathElement(root, 0, 1), new MatcherPathElement(rule1, 1, 1)),
        Arrays.asList(new MatcherPathElement(root, 0, 1), new MatcherPathElement(rule2, 1, 1), new MatcherPathElement(rule3, 1, 1)));
    String result = formatter.format(new ParseError(inputBuffer, 5, "expected: IDENTIFIER", failedPaths));
    System.out.print(result);
    String expected = new StringBuilder()
        .append("At line 2 column 2 expected: IDENTIFIER\n")
        .append("1: foo\n")
        .append("2: bar\n")
        .append("    ^\n")
        .append("3: baz\n")
        .append("Failed at:\n")
        .append("┌─rule1\n")
        .append("│ ┌─rule3\n")
        .append("├─rule2\n")
        .append("root matched (1, 1)-(1, 2)\n")
        .toString();
    assertThat(result).isEqualTo(expected);
  }

}
