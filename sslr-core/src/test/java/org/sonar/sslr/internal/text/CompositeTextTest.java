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

import org.junit.Before;
import org.junit.Test;
import org.sonar.sslr.text.TextCursor;
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
    AbstractText t1 = new FileText(file1, "foo".toCharArray());
    file2 = new File("bar");
    AbstractText t2 = new FileText(file2, "bar".toCharArray());
    file3 = new File("baz");
    AbstractText t3 = new FileText(file3, "baz".toCharArray());
    text = new CompositeText(Arrays.asList(t1, t2, t3));
  }

  @Test
  public void test_length() {
    assertThat(text.length()).isEqualTo(9);
    assertThat(text.cursor().length()).isEqualTo(9);
  }

  @Test
  public void test_cursor_getText() {
    assertThat(text.cursor().getText()).isSameAs(text);
  }

  @Test
  public void test_cursor_charAt() {
    TextCursor cursor = text.cursor();
    assertThat(cursor.charAt(3)).isEqualTo('b');
    assertThat(cursor.charAt(0)).isEqualTo('f');
  }

  @Test
  public void test_cursor_subText() {
    assertThat(text.cursor().subText(0, 3)).isInstanceOf(SubText.class);
    assertThat(text.cursor().subText(0, 3).toString()).isEqualTo("foo");
    assertThat(text.cursor().subText(2, 4).toString()).isEqualTo("ob");
  }

  @Test
  public void test_cursor_getLocation() {
    assertThat(text.cursor().getLocation(0)).isEqualTo(new TextLocation(file1, 1, 1));
    assertThat(text.cursor().getLocation(2)).isEqualTo(new TextLocation(file1, 1, 3));
    assertThat(text.cursor().getLocation(3)).isEqualTo(new TextLocation(file2, 1, 1));
    assertThat(text.cursor().getLocation(5)).isEqualTo(new TextLocation(file2, 1, 3));
    assertThat(text.cursor().getLocation(6)).isEqualTo(new TextLocation(file3, 1, 1));
    assertThat(text.cursor().getLocation(8)).isEqualTo(new TextLocation(file3, 1, 3));
    assertThat(text.cursor().getLocation(9)).isEqualTo(new TextLocation(file3, 1, 4));
  }

  @Test
  public void test_cursor_toString() {
    assertThat(text.cursor().toString()).isEqualTo("foobarbaz");
  }

  @Test
  public void test_pattern() {
    assertThat(Pattern.matches("f.*z", text.cursor())).isTrue();
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
