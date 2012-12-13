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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TextLineImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void length() {
    assertThat(new TextLineImpl(mock(Text.class), 0, 0, 0).length()).isEqualTo(0);
    assertThat(new TextLineImpl(mock(Text.class), 0, 2, 2).length()).isEqualTo(2);
    assertThat(new TextLineImpl(mock(Text.class), 0, 5, 15).length()).isEqualTo(5);
  }

  @Test
  public void charAt() {
    Text text = mock(Text.class);
    when(text.charAt(0)).thenReturn('0');
    when(text.charAt(42)).thenReturn('4');
    when(text.charAt(43)).thenReturn('2');

    TextLineImpl line = new TextLineImpl(text, 0, 100, 100);
    assertThat(line.charAt(0)).isEqualTo('0');

    line = new TextLineImpl(text, 42, 100, 100);
    assertThat(line.charAt(0)).isEqualTo('4');
    assertThat(line.charAt(1)).isEqualTo('2');
  }

  @Test
  public void should_not_give_characters_beyond_line_length() {
    thrown.expect(IndexOutOfBoundsException.class);
    new TextLineImpl(mock(Text.class), 0, 0, 100).charAt(0);
  }

  @Test
  public void subSequence() {
    Text text = mock(Text.class);
    Text subText1 = mock(Text.class);
    Text subText2 = mock(Text.class);

    when(text.subSequence(42, 52)).thenReturn(subText1);
    when(text.subSequence(62, 77)).thenReturn(subText2);

    TextLineImpl line = new TextLineImpl(text, 42, 100, 100);
    assertThat(line.subSequence(0, 10)).isSameAs(subText1);
    assertThat(line.subSequence(20, 35)).isSameAs(subText2);
  }

  @Test
  public void should_not_give_sub_sequences_beyond_line_length() {
    thrown.expect(IndexOutOfBoundsException.class);
    new TextLineImpl(mock(Text.class), 0, 0, 100).subSequence(0, 1);
  }

  @Test
  public void getLineNumber() {
    Position position = mock(Position.class);
    when(position.getLine()).thenReturn(42);
    Text text = mock(Text.class);
    when(text.getPosition(7)).thenReturn(position);

    assertThat(new TextLineImpl(text, 7, 52, 62).getLineNumber()).isEqualTo(42);
  }

  @Test
  public void should_return_one_as_line_number_on_empty_string() {
    assertThat(new TextLineImpl(mock(Text.class), 0, 0, 0).getLineNumber()).isEqualTo(1);
  }

  @Test
  public void getIndex() {
    assertThat(new TextLineImpl(mock(Text.class), 0, 0, 0).getIndex()).isEqualTo(0);
    assertThat(new TextLineImpl(mock(Text.class), 42, 0, 0).getIndex()).isEqualTo(42);
  }

  @Test
  public void getLineTerminator() {
    Text text = mock(Text.class);
    Text lineTerminator = mock(Text.class);

    when(text.subSequence(10, 12)).thenReturn(lineTerminator);

    assertThat(new TextLineImpl(text, 0, 10, 12).getLineTerminator()).isSameAs(lineTerminator);
  }

  @Test
  public void getText() {
    Text text = mock(Text.class);
    assertThat(new TextLineImpl(text, 0, 0, 0).getText()).isSameAs(text);
  }

}
