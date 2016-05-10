/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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

import org.junit.Before;
import org.junit.Test;
import org.sonar.sslr.internal.matchers.ImmutableInputBuffer;
import org.sonar.sslr.internal.matchers.InputBuffer;

import static org.fest.assertions.Assertions.assertThat;

public class ParseErrorFormatterTest {

  private ParseErrorFormatter formatter;

  @Before
  public void setUp() {
    formatter = new ParseErrorFormatter();
  }

  @Test
  public void test() {
    InputBuffer inputBuffer = new ImmutableInputBuffer("\t2+4*10-0*\n".toCharArray());
    String result = formatter.format(new ParseError(inputBuffer, 10));
    System.out.print(result);
    String expected = new StringBuilder()
        .append("Parse error at line 1 column 11:\n")
        .append('\n')
        .append("1:  2+4*10-0*\n")
        .append("             ^\n")
        .append("2: \n")
        .toString();

    assertThat(result).isEqualTo(expected);
  }

}
