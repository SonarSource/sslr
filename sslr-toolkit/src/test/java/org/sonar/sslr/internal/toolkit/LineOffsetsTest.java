/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.internal.toolkit;

import org.sonar.sslr.internal.toolkit.LineOffsets;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.net.URISyntaxException;

import static org.fest.assertions.Assertions.assertThat;

public class LineOffsetsTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void getStartOffset() {
    Token foo = mockToken(1, 0, "foo");
    Token bar = mockToken(2, 2, "bar");

    LineOffsets lineOffsets = new LineOffsets("foo\n??bar");

    assertThat(lineOffsets.getStartOffset(foo)).isEqualTo(0);
    assertThat(lineOffsets.getStartOffset(bar)).isEqualTo(6);
  }

  @Test
  public void getEndOffsetSingleLine() {
    Token foo = mockToken(1, 0, "foo");
    Token bar = mockToken(2, 2, "bar");

    LineOffsets lineOffsets = new LineOffsets("foo\n??bar...");

    assertThat(lineOffsets.getEndOffset(foo)).isEqualTo(3);
    assertThat(lineOffsets.getEndOffset(bar)).isEqualTo(9);
  }

  @Test
  public void getEndOffsetMultiLine() {
    Token foo = mockToken(1, 0, "foo");
    Token bar = mockToken(2, 2, "bar\nbaz");

    LineOffsets lineOffsets = new LineOffsets("foo\n??bar\nbaz...");

    assertThat(lineOffsets.getEndOffset(foo)).isEqualTo(3);
    assertThat(lineOffsets.getEndOffset(bar)).isEqualTo(13);
  }

  @Test
  public void getEndOffsetMultiLineRNSingleOffsetIncrement() {
    Token foo = mockToken(1, 0, "foo");
    Token bar = mockToken(2, 2, "bar\r\nbaz");

    LineOffsets lineOffsets = new LineOffsets("foo\n??bar\r\nbaz...");

    assertThat(lineOffsets.getEndOffset(foo)).isEqualTo(3);
    assertThat(lineOffsets.getEndOffset(bar)).isEqualTo(13);
  }

  @Test
  public void getEndOffsetMultiLineRNewLine() {
    Token foo = mockToken(1, 0, "foo");
    Token bar = mockToken(2, 2, "bar\rbaz");

    LineOffsets lineOffsets = new LineOffsets("foo\n??bar\rbaz...");

    assertThat(lineOffsets.getEndOffset(foo)).isEqualTo(3);
    assertThat(lineOffsets.getEndOffset(bar)).isEqualTo(13);
  }

  @Test
  public void getOffset() {
    LineOffsets lineOffsets = new LineOffsets("int a = 0;\nint b = 0;");

    assertThat(lineOffsets.getOffset(2, 4)).isEqualTo(15);
    assertThat(lineOffsets.getOffset(2, 100)).isEqualTo(21);
    assertThat(lineOffsets.getOffset(100, 100)).isEqualTo(21);
  }

  @Test
  public void getOffsetCariageReturnAsNewLine() {
    LineOffsets lineOffsets = new LineOffsets("\rfoo");

    assertThat(lineOffsets.getOffset(1, 0)).isEqualTo(0);
    assertThat(lineOffsets.getOffset(2, 0)).isEqualTo(1);
  }

  @Test
  public void getOffsetCariageReturnAndLineFeedAsSingleOffset() {
    LineOffsets lineOffsets = new LineOffsets("\r\nfoo");

    assertThat(lineOffsets.getOffset(1, 0)).isEqualTo(0);
    assertThat(lineOffsets.getOffset(2, 0)).isEqualTo(1);
  }

  @Test
  public void getOffsetBadLine() {
    thrown.expect(IllegalArgumentException.class);

    LineOffsets lineOffsets = new LineOffsets("");
    lineOffsets.getOffset(0, 0);
  }

  @Test
  public void getOffsetBadColumn() {
    thrown.expect(IllegalArgumentException.class);

    LineOffsets lineOffsets = new LineOffsets("");
    lineOffsets.getOffset(1, -1);
  }

  public static Token mockToken(int line, int column, String value) {
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
