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
package com.sonar.sslr.toolkit;

import com.sonar.sslr.toolkit.Offsets;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.fest.assertions.Assertions.assertThat;

public class OffsetsTest {

  @Test
  public void test() {
    Offsets offsets = new Offsets();
    offsets.computeLineOffsets("line1\nline2\n", 15);

    assertThat(offsets.getOffset(1, 1)).isEqualTo(2);
    assertThat(offsets.getOffset(1, 2)).isEqualTo(3);
    assertThat(offsets.getOffset(2, 1)).isEqualTo(8);
    assertThat(offsets.getOffset(2, 2)).isEqualTo(9);
    assertThat(offsets.getOffset(3, 1)).isEqualTo(14);
    assertThat(offsets.getOffset(3, 2)).isEqualTo(14);
    assertThat(offsets.getOffset(4, 1)).isEqualTo(14);

    assertThat(offsets.getLineFromOffset(2)).isEqualTo(1);
    assertThat(offsets.getLineFromOffset(3)).isEqualTo(1);
    assertThat(offsets.getLineFromOffset(8)).isEqualTo(2);
    assertThat(offsets.getLineFromOffset(9)).isEqualTo(2);
    assertThat(offsets.getLineFromOffset(14)).isEqualTo(3);
    assertThat(offsets.getLineFromOffset(15)).isEqualTo(3);

    assertThat(offsets.getColumnFromOffsetAndLine(2, 1)).isEqualTo(1);
    assertThat(offsets.getColumnFromOffsetAndLine(3, 1)).isEqualTo(2);
    assertThat(offsets.getColumnFromOffsetAndLine(8, 2)).isEqualTo(1);
    assertThat(offsets.getColumnFromOffsetAndLine(9, 2)).isEqualTo(2);
    assertThat(offsets.getColumnFromOffsetAndLine(14, 3)).isEqualTo(1);
    assertThat(offsets.getColumnFromOffsetAndLine(15, 3)).isEqualTo(2);

    Token token = mockToken(1, 1, "l");
    assertThat(offsets.getStartOffset(token)).isEqualTo(2);
    assertThat(offsets.getEndOffset(token)).isEqualTo(3);

    token = mockToken(1, 1, "line1\nl");
    assertThat(offsets.getStartOffset(token)).isEqualTo(2);
    assertThat(offsets.getEndOffset(token)).isEqualTo(8);
  }

  private static Token mockToken(int line, int column, String value) {
    try {
      return Token.builder()
          .setLine(line)
          .setColumn(column)
          .setValueAndOriginalValue(value)
          .setType(GenericTokenType.IDENTIFIER)
          .setURI(new URI("tests://unittest"))
          .build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}
