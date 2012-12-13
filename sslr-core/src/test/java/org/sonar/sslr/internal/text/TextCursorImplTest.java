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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.text.Position;
import org.sonar.sslr.text.Text;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TextCursorImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_start_cursor_at_zero() {
    TextCursorImpl cursor = new TextCursorImpl(new TextImpl("foobar"));

    assertThat(cursor.getIndex()).isEqualTo(0);
    assertThat(cursor.length()).isEqualTo(6);
    assertThat(cursor.charAt(0)).isEqualTo('f');
    assertThat(cursor.subSequence(0, 1).charAt(0)).isEqualTo('f');
    assertThat(cursor.getPosition()).isEqualTo(new Position(1, 1));
    assertThat(cursor.getOriginalPosition()).isEqualTo(new Position(null, 1, 1));
  }

  @Test
  public void should_move_cursor_forward() {
    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.append(new TextImpl("abc\r"));
    File originalFile = mock(File.class);
    textBuilder.append(new TextImpl("\ne".toCharArray(), originalFile));
    textBuilder.append(new TextImpl("df"));
    TextImpl text = new TextImpl(textBuilder);

    TextCursorImpl cursor = new TextCursorImpl(text);

    cursor.moveForward(1);
    assertThat(cursor.getIndex()).isEqualTo(1);
    assertThat(cursor.length()).isEqualTo(7);
    assertThat(cursor.charAt(0)).isEqualTo('b');
    assertThat(cursor.subSequence(0, 1).charAt(0)).isEqualTo('b');
    assertThat(cursor.getPosition()).isEqualTo(new Position(1, 2));
    assertThat(cursor.getOriginalPosition()).isEqualTo(new Position(null, 1, 2));

    cursor.moveForward(4);
    assertThat(cursor.getIndex()).isEqualTo(5);
    assertThat(cursor.length()).isEqualTo(3);
    assertThat(cursor.charAt(0)).isEqualTo('e');
    assertThat(cursor.subSequence(0, 1).charAt(0)).isEqualTo('e');
    assertThat(cursor.getPosition()).isEqualTo(new Position(2, 1));
    assertThat(cursor.getOriginalPosition()).isEqualTo(new Position(originalFile, 2, 1));

    cursor.moveForward(3);
    assertThat(cursor.length()).isEqualTo(0);
  }

  @Test
  public void charAt() {
    TextCursorImpl cursor = new TextCursorImpl(new TextImpl("foobar"));

    assertThat(cursor.charAt(0)).isEqualTo('f');
    assertThat(cursor.charAt(1)).isEqualTo('o');
    assertThat(cursor.charAt(2)).isEqualTo('o');
    assertThat(cursor.charAt(3)).isEqualTo('b');
    assertThat(cursor.charAt(4)).isEqualTo('a');
    assertThat(cursor.charAt(5)).isEqualTo('r');
  }

  @Test
  public void subSequence() {
    TextCursorImpl cursor = new TextCursorImpl(new TextImpl("foobar"));

    Text foo = cursor.subSequence(0, 3);
    assertThat(foo.length()).isEqualTo(3);
    assertThat(foo.charAt(0)).isEqualTo('f');
    assertThat(foo.charAt(1)).isEqualTo('o');
    assertThat(foo.charAt(2)).isEqualTo('o');

    Text ba = cursor.subSequence(3, 5);
    assertThat(ba.length()).isEqualTo(2);
    assertThat(ba.charAt(0)).isEqualTo('b');
    assertThat(ba.charAt(1)).isEqualTo('a');
  }

  @Test
  public void should_not_move_forward_beyond_length() {
    thrown.expect(IndexOutOfBoundsException.class);
    new TextCursorImpl(new TextImpl("")).moveForward(1);
  }

  @Test
  public void should_not_move_forward_with_zero_offset() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("offset(0) >= 1");
    new TextCursorImpl(new TextImpl("")).moveForward(0);
  }

  @Test
  public void getText() {
    Text text = mock(Text.class);
    assertThat(new TextCursorImpl(text).getText()).isSameAs(text);
  }

}
