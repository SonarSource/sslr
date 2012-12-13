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
import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextLine;

import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TextLineCursorImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_return_one_line_for_the_empty_string() {
    Iterator<TextLine> it = new TextImpl("").lines().iterator();

    assertThat(it.hasNext()).isTrue();
    TextLine lineOne = it.next();
    assertThat(lineOne.getLineNumber()).isEqualTo(1);
    assertThat(lineOne.length()).isEqualTo(0);
    assertThat(lineOne.getLineTerminator().length()).isEqualTo(0);

    assertThat(it.hasNext()).isFalse();
  }

  @Test
  public void should_return_full_line_when_no_line_terminators() {
    Iterator<TextLine> it = new TextImpl("foo").lines().iterator();

    assertThat(it.hasNext()).isTrue();
    TextLine lineOne = it.next();
    assertThat(lineOne.getLineNumber()).isEqualTo(1);
    assertThat(lineOne.length()).isEqualTo(3);
    assertThat(lineOne.getLineTerminator().length()).isEqualTo(0);

    assertThat(it.hasNext()).isFalse();
  }

  @Test
  public void should_return_two_lines_with_a_lf_line_terminator() {
    Iterator<TextLine> it = new TextImpl("foo\nbar").lines().iterator();

    assertThat(it.hasNext()).isTrue();
    TextLine lineOne = it.next();
    assertThat(lineOne.getLineNumber()).isEqualTo(1);
    assertThat(lineOne.length()).isEqualTo(3);
    assertThat(lineOne.getLineTerminator().length()).isEqualTo(1);
    assertThat(lineOne.getLineTerminator().charAt(0)).isEqualTo('\n');

    assertThat(it.hasNext()).isTrue();
    TextLine lineTwo = it.next();
    assertThat(lineTwo.getLineNumber()).isEqualTo(2);
    assertThat(lineTwo.length()).isEqualTo(3);
    assertThat(lineTwo.getLineTerminator().length()).isEqualTo(0);

    assertThat(it.hasNext()).isFalse();
  }

  @Test
  public void should_return_two_lines_with_a_crlf_line_terminator() {
    Iterator<TextLine> it = new TextImpl("foo\r\nbar").lines().iterator();

    assertThat(it.hasNext()).isTrue();
    TextLine lineOne = it.next();
    assertThat(lineOne.getLineNumber()).isEqualTo(1);
    assertThat(lineOne.length()).isEqualTo(3);
    assertThat(lineOne.getLineTerminator().length()).isEqualTo(2);
    assertThat(lineOne.getLineTerminator().charAt(0)).isEqualTo('\r');
    assertThat(lineOne.getLineTerminator().charAt(1)).isEqualTo('\n');

    assertThat(it.hasNext()).isTrue();
    TextLine lineTwo = it.next();
    assertThat(lineTwo.getLineNumber()).isEqualTo(2);
    assertThat(lineTwo.length()).isEqualTo(3);
    assertThat(lineTwo.getLineTerminator().length()).isEqualTo(0);

    assertThat(it.hasNext()).isFalse();
  }

  @Test
  public void remove() {
    thrown.expect(UnsupportedOperationException.class);
    new TextLineCursorImpl(mock(Text.class)).remove();
  }

}
