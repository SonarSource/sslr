/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
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
package org.sonar.sslr.internal.vm.lexerful;

import com.sonar.sslr.api.Token;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LexerfulParseErrorFormatterTest {

  @Test
  public void test() {
    List<Token> tokens = Arrays.asList(
        token(2, 1, "foo\nbar\nbaz"),
        token(4, 6, "qux"),
        token(6, 3, "end"));
    String expected = new StringBuilder()
        .append("Parse error at line 4 column 6:\n")
        .append("\n")
        .append("    2: foo\n")
        .append("    3: bar\n")
        .append("  -->  baz   qux\n")
        .append("    5: \n")
        .append("    6:    end\n")
        .toString();
    assertThat(new LexerfulParseErrorFormatter().format(tokens, 1)).isEqualTo(expected);
  }

  private static Token token(int line, int column, String value) {
    Token token = mock(Token.class);
    when(token.getLine()).thenReturn(line);
    when(token.getColumn()).thenReturn(column);
    when(token.getOriginalValue()).thenReturn(value);
    return token;
  }

}
