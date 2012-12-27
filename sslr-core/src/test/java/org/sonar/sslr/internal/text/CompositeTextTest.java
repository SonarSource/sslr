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
  private CompositeText text;

  @Before
  public void setUp() {
    file1 = new File("foo");
    AbstractText t1 = new FileText(file1, "foo".toCharArray());
    file2 = new File("bar");
    AbstractText t2 = new FileText(file2, "bar".toCharArray());
    text = new CompositeText(Arrays.asList(t1, t2));
  }

  @Test
  public void test_length() {
    assertThat(text.length()).isEqualTo(6);
    assertThat(text.cursor().length()).isEqualTo(6);
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
    assertThat(text.cursor().getLocation(3)).isEqualTo(new TextLocation(file2, 1, 1));
  }

  @Test
  public void test_pattern() {
    assertThat(Pattern.matches("f.*r", text.cursor())).isTrue();
  }

}
