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
import org.sonar.sslr.text.TextCharSequence;
import org.sonar.sslr.text.TextLocation;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.fest.assertions.Assertions.assertThat;

public class CompositeTextTest {

  private File file1;
  private File file2;
  private File file3;
  private CompositeText text;

  @Before
  public void setUp() {
    file1 = new File("foo");
    AbstractText t1 = new LocatedText(file1, "foo".toCharArray());
    file2 = new File("bar");
    AbstractText t2 = new LocatedText(file2, "bar".toCharArray());
    file3 = new File("baz");
    AbstractText t3 = new LocatedText(file3, "baz".toCharArray());
    text = new CompositeText(Arrays.asList(t1, t2, t3));
  }

  @Test
  public void test_length() {
    assertThat(text.length()).isEqualTo(9);
    assertThat(text.sequence().length()).isEqualTo(9);
  }

  @Test
  public void test_sequence_getText() {
    assertThat(text.sequence().getText()).isSameAs(text);
  }

  @Test
  public void test_sequence_charAt() {
    TextCharSequence sequence = text.sequence();
    assertThat(sequence.charAt(3)).isEqualTo('b');
    assertThat(sequence.charAt(0)).isEqualTo('f');
  }

  @Test
  public void test_sequence_subText() {
    assertThat(text.sequence().subText(0, 3)).isInstanceOf(SubText.class);
    assertThat(text.sequence().subText(0, 3).toString()).isEqualTo("foo");
    assertThat(text.sequence().subText(2, 4).toString()).isEqualTo("ob");
  }

  @Test
  public void test_sequence_getLocation() {
    assertThat(text.sequence().getLocation(0)).isEqualTo(new TextLocation(file1, 1, 1));
    assertThat(text.sequence().getLocation(2)).isEqualTo(new TextLocation(file1, 1, 3));
    assertThat(text.sequence().getLocation(3)).isEqualTo(new TextLocation(file2, 1, 1));
    assertThat(text.sequence().getLocation(5)).isEqualTo(new TextLocation(file2, 1, 3));
    assertThat(text.sequence().getLocation(6)).isEqualTo(new TextLocation(file3, 1, 1));
    assertThat(text.sequence().getLocation(8)).isEqualTo(new TextLocation(file3, 1, 3));
    assertThat(text.sequence().getLocation(9)).isEqualTo(new TextLocation(file3, 1, 4));
  }

  @Test
  public void test_sequence_toString() {
    assertThat(text.sequence().toString()).isEqualTo("foobarbaz");
  }

  @Test
  public void test_pattern() {
    assertThat(Pattern.matches("f.*z", text.sequence())).isTrue();
  }

  @Test
  public void test_toCharArray() {
    char[] dest;

    dest = new char[1];
    text.toCharArray(1, dest, 0, 1);
    assertThat(dest).isEqualTo(new char[] {'o'});

    dest = new char[4];
    text.toCharArray(3, dest, 0, 4);
    assertThat(dest).isEqualTo(new char[] {'b', 'a', 'r', 'b'});

    dest = new char[7];
    text.toCharArray(2, dest, 0, 7);
    assertThat(dest).isEqualTo(new char[] {'o', 'b', 'a', 'r', 'b', 'a', 'z'});
  }

}
