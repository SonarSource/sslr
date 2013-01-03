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
import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextCharSequence;
import org.sonar.sslr.text.TextLocation;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubTextTest {

  private AbstractText originalText;
  private TextCharSequence innerCursor;
  private SubText text;

  @Before
  public void setUp() {
    originalText = mock(AbstractText.class);
    innerCursor = mock(TextCharSequence.class);
    when(originalText.sequence()).thenReturn(innerCursor);
    text = new SubText(originalText, 1, 3);
  }

  @Test
  public void test_length() {
    assertThat(text.length()).isEqualTo(2);
    assertThat(text.sequence().length()).isEqualTo(2);
  }

  @Test
  public void test_sequence_getText() {
    assertThat(text.sequence().getText()).isSameAs(text);
  }

  @Test
  public void test_sequence_subText() {
    assertThat(text.sequence().subText(0, 2)).isSameAs(text);

    Text result = mock(Text.class);
    when(innerCursor.subText(2, 3)).thenReturn(result);
    assertThat(text.sequence().subText(1, 2)).isSameAs(result);
  }

  @Test
  public void test_sequence_charAt() {
    when(innerCursor.charAt(2)).thenReturn('T');
    assertThat(text.sequence().charAt(1)).isEqualTo('T');
  }

  @Test
  public void test_sequence_getLocation() {
    TextLocation result = mock(TextLocation.class);
    when(innerCursor.getLocation(2)).thenReturn(result);
    assertThat(text.sequence().getLocation(1)).isSameAs(result);
  }

  @Test
  public void test_sequence_toString() {
    originalText = new PlainText(new char[] {'b', 'a', 'r'});
    text = new SubText(originalText, 1, 2);
    assertThat(text.sequence().toString()).isEqualTo("a");
  }

  @Test
  public void test_getTransformationDepth() {
    when(originalText.getTransformationDepth()).thenReturn(42);
    assertThat(text.getTransformationDepth()).isEqualTo(42);
  }

}
