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
package org.sonar.sslr.internal.text;

import org.junit.Test;
import org.sonar.sslr.text.Position;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TextUtilsTest {

  // Array of characters

  @Test
  public void array_isCrLf() {
    assertThat(TextUtils.isCrLf("".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCrLf("\r".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCrLf("\n".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCrLf("\r_".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCrLf("\n_".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCrLf("\n\r".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCrLf("_\r\n".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCrLf("_\r\n".toCharArray(), 2)).isFalse();

    assertThat(TextUtils.isCrLf("\r\n".toCharArray(), 0)).isTrue();
    assertThat(TextUtils.isCrLf("_\r\n".toCharArray(), 1)).isTrue();
  }

  @Test
  public void array_isCrOrLf() {
    assertThat(TextUtils.isCrOrLf("_".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCrOrLf("_\n".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCrOrLf("_\n_".toCharArray(), 2)).isFalse();

    assertThat(TextUtils.isCrOrLf("\r".toCharArray(), 0)).isTrue();
    assertThat(TextUtils.isCrOrLf("\n".toCharArray(), 0)).isTrue();
    assertThat(TextUtils.isCrOrLf("_\n_".toCharArray(), 1)).isTrue();
  }

  @Test
  public void array_isCr() {
    assertThat(TextUtils.isCr("_".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCr("\n".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isCr("_\r".toCharArray(), 0)).isFalse();

    assertThat(TextUtils.isCr("\r".toCharArray(), 0)).isTrue();
    assertThat(TextUtils.isCr("_\r".toCharArray(), 1)).isTrue();
  }

  @Test
  public void array_isLf() {
    assertThat(TextUtils.isLf("_".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isLf("\r".toCharArray(), 0)).isFalse();
    assertThat(TextUtils.isLf("_\n".toCharArray(), 0)).isFalse();

    assertThat(TextUtils.isLf("\n".toCharArray(), 0)).isTrue();
    assertThat(TextUtils.isLf("_\n".toCharArray(), 1)).isTrue();
  }

  // CharSequence

  @Test
  public void char_sequence_isCrLf() {
    assertThat(TextUtils.isCrLf("", 0)).isFalse();
    assertThat(TextUtils.isCrLf("\r", 0)).isFalse();
    assertThat(TextUtils.isCrLf("\n", 0)).isFalse();
    assertThat(TextUtils.isCrLf("\r_", 0)).isFalse();
    assertThat(TextUtils.isCrLf("\n_", 0)).isFalse();
    assertThat(TextUtils.isCrLf("\n\r", 0)).isFalse();
    assertThat(TextUtils.isCrLf("_\r\n", 0)).isFalse();
    assertThat(TextUtils.isCrLf("_\r\n", 2)).isFalse();

    assertThat(TextUtils.isCrLf("\r\n", 0)).isTrue();
    assertThat(TextUtils.isCrLf("_\r\n", 1)).isTrue();
  }

  @Test
  public void char_sequence_isCrOrLf() {
    assertThat(TextUtils.isCrOrLf("_", 0)).isFalse();
    assertThat(TextUtils.isCrOrLf("_\n", 0)).isFalse();
    assertThat(TextUtils.isCrOrLf("_\n_", 2)).isFalse();

    assertThat(TextUtils.isCrOrLf("\r", 0)).isTrue();
    assertThat(TextUtils.isCrOrLf("\n", 0)).isTrue();
    assertThat(TextUtils.isCrOrLf("_\n_", 1)).isTrue();
  }

  @Test
  public void char_sequence_isCr() {
    assertThat(TextUtils.isCr("_", 0)).isFalse();
    assertThat(TextUtils.isCr("\n", 0)).isFalse();
    assertThat(TextUtils.isCr("_\r", 0)).isFalse();

    assertThat(TextUtils.isCr("\r", 0)).isTrue();
    assertThat(TextUtils.isCr("_\r", 1)).isTrue();
  }

  @Test
  public void char_sequence_isLf() {
    assertThat(TextUtils.isLf("_", 0)).isFalse();
    assertThat(TextUtils.isLf("\r", 0)).isFalse();
    assertThat(TextUtils.isLf("_\n", 0)).isFalse();

    assertThat(TextUtils.isLf("\n", 0)).isTrue();
    assertThat(TextUtils.isLf("_\n", 1)).isTrue();
  }

  // Positions

  @Test
  public void getPositions() {
    assertThat(TextUtils.getPositions("".toCharArray())).isEmpty();
    assertThat(TextUtils.getPositions("a".toCharArray())).isEqualTo(new Position[] {new Position(1, 1)});
    assertThat(TextUtils.getPositions("ab".toCharArray())).isEqualTo(new Position[] {new Position(1, 1), new Position(1, 2)});
    assertThat(TextUtils.getPositions("a\nb".toCharArray())).isEqualTo(new Position[] {new Position(1, 1), new Position(1, 2), new Position(2, 1)});
    assertThat(TextUtils.getPositions("a\rb".toCharArray())).isEqualTo(new Position[] {new Position(1, 1), new Position(1, 2), new Position(2, 1)});
    assertThat(TextUtils.getPositions("a\r\nb".toCharArray())).isEqualTo(new Position[] {new Position(1, 1), new Position(1, 2), new Position(1, 3), new Position(2, 1)});
    assertThat(TextUtils.getPositions("\n".toCharArray())).isEqualTo(new Position[] {new Position(1, 1)});
    assertThat(TextUtils.getPositions("\r".toCharArray())).isEqualTo(new Position[] {new Position(1, 1)});
  }

  @Test
  public void getPositionsWithFile() {
    File originalFile = mock(File.class);

    assertThat(TextUtils.getPositionsWithFile(new Position[] {}, originalFile)).isEmpty();
    assertThat(TextUtils.getPositionsWithFile(new Position[] {new Position(1, 2)}, originalFile)).isEqualTo(new Position[] {new Position(originalFile, 1, 2)});
    assertThat(TextUtils.getPositionsWithFile(new Position[] {new Position(24, 42)}, originalFile)).isEqualTo(new Position[] {new Position(originalFile, 24, 42)});

    assertThat(TextUtils.getPositionsWithFile(new Position[] {new Position(1, 2), new Position(24, 42)}, originalFile))
        .isEqualTo(new Position[] {new Position(originalFile, 1, 2), new Position(originalFile, 24, 42)});
  }

}
