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

import org.junit.Test;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;
import org.sonar.sslr.internal.matchers.ImmutableInputBuffer;
import org.sonar.sslr.internal.matchers.InputBuffer;
import org.sonar.sslr.internal.matchers.MatcherPathElement;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class ParseErrorFormatterTest {

  @Test
  public void test() {
    InputBuffer inputBuffer = new ImmutableInputBuffer(" 2+4*10-0*\n".toCharArray());
    ParseErrorFormatter formatter = new ParseErrorFormatter();
    MatcherPathElement root = new MatcherPathElement(new GrammarElementMatcher("root"), 0, 1);
    MatcherPathElement expression = new MatcherPathElement(new GrammarElementMatcher("expression"), 1, 8);
    MatcherPathElement term = new MatcherPathElement(new GrammarElementMatcher("term"), 8, 10);
    MatcherPathElement factor = new MatcherPathElement(new GrammarElementMatcher("factor"), 10, 10);
    MatcherPathElement number = new MatcherPathElement(new GrammarElementMatcher("number"), 10, 10);
    MatcherPathElement parens = new MatcherPathElement(new GrammarElementMatcher("parens"), 10, 10);
    MatcherPathElement lpar = new MatcherPathElement(new GrammarElementMatcher("lpar"), 10, 10);
    MatcherPathElement variable = new MatcherPathElement(new GrammarElementMatcher("variable"), 10, 10);
    List<List<MatcherPathElement>> failedPaths = Arrays.asList(
        Arrays.asList(root, expression, term, factor, number),
        Arrays.asList(root, expression, term, factor, parens, lpar),
        Arrays.asList(root, expression, term, factor, variable));
    String result = formatter.format(new ParseError(inputBuffer, 10, "expected one of: number lpar variable", failedPaths));
    System.out.print(result);
    String expected = new StringBuilder()
        .append("Parse error at line 1 column 11 expected one of: number lpar variable\n")
        .append('\n')
        .append("1:  2+4*10-0*\n")
        .append("             ^\n")
        .append("2: \n")
        .append('\n')
        .append("Failed at rules:\n")
        .append("  ┌─number\n")
        .append("  │ ┌─lpar\n")
        .append("  ├─parens\n")
        .append("  ├─variable\n")
        .append("┌─factor\n")
        .append("term consumed from (1, 9) to (1, 10): \"0*\"\n")
        .append("expression consumed from (1, 2) to (1, 8): \"2+4*10-\"\n")
        .append("root consumed from (1, 1) to (1, 1): \" \"\n")
        .toString();
    assertThat(result).isEqualTo(expected);
  }

}
