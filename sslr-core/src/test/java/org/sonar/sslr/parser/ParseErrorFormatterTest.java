/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
