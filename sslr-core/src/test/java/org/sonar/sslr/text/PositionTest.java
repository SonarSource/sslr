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
package org.sonar.sslr.text;

import org.junit.Test;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PositionTest {

  @Test
  public void should_construct_position_without_file() {
    Position position = new Position(24, 42);

    assertThat(position.getFile()).isNull();
    assertThat(position.getLine()).isEqualTo(24);
    assertThat(position.getColumn()).isEqualTo(42);
    assertThat(position.toString()).isEqualTo("Position{file=null, line=24, column=42}");
  }

  @Test
  public void should_construct_position_with_file() {
    File file = mock(File.class);
    Position position = new Position(file, 24, 42);

    assertThat(position.getFile()).isSameAs(file);
    assertThat(position.getLine()).isEqualTo(24);
    assertThat(position.getColumn()).isEqualTo(42);
    assertThat(position.toString()).startsWith("Position{file=Mock for File, hashCode: " + file.hashCode() + ", line=24, column=42}");
  }

  @Test
  public void should_override_hashCode_and_equals() {
    assertThat(new Position(1, 1)).isNotEqualTo(mock(Object.class));

    Position position = new Position(1, 1);
    assertThat(position).isEqualTo(position);
    assertThat(position.hashCode()).isEqualTo(position.hashCode());

    File file = mock(File.class);

    assertThat(new Position(1, 2)).isNotEqualTo(new Position(2, 2));
    assertThat(new Position(1, 2)).isNotEqualTo(new Position(1, 3));
    assertThat(new Position(1, 2)).isEqualTo(new Position(1, 2));
    assertThat(new Position(1, 2).hashCode()).isEqualTo(new Position(1, 2).hashCode());

    assertThat(new Position(3, 4)).isNotEqualTo(new Position(file, 3, 4));
    assertThat(new Position(file, 3, 4)).isEqualTo(new Position(file, 3, 4));
    assertThat(new Position(file, 3, 4).hashCode()).isEqualTo(new Position(file, 3, 4).hashCode());
  }

}
