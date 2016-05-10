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
package org.sonar.sslr.internal.text;

import org.junit.Before;
import org.junit.Test;
import org.sonar.sslr.text.TextLocation;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class LocatedTextTest {

  private File file;
  private LocatedText text;

  @Before
  public void setUp() {
    file = new File("foo");
    text = new LocatedText(file, "foo\nbar".toCharArray());
  }

  @Test
  public void test_sequence_getLocation() {
    assertThat(text.getLocation(1)).isEqualTo(new TextLocation(file, 1, 2));
    assertThat(text.getLocation(4)).isEqualTo(new TextLocation(file, 2, 1));
    assertThat(text.getLocation(5)).isEqualTo(new TextLocation(file, 2, 2));
    assertThat(text.getLocation(6)).isEqualTo(new TextLocation(file, 2, 3));
    assertThat(text.getLocation(7)).isEqualTo(new TextLocation(file, 2, 4));
  }

}
