/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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
import org.sonar.sslr.text.TextCharSequence;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransformedTextTest {

  private AbstractText fromText, toText;
  private TransformedText text;

  @Before
  public void setUp() {
    fromText = mock(AbstractText.class);
    toText = mock(AbstractText.class);
    text = new TransformedText(fromText, toText);
  }

  @Test
  public void test_length() {
    when(toText.length()).thenReturn(42);
    assertThat(text.length()).isEqualTo(42);
  }

  @Test
  public void test_sequence() {
    TextCharSequence sequence = mock(TextCharSequence.class);
    when(toText.sequence()).thenReturn(sequence);
    assertThat(text.sequence()).isSameAs(sequence);
  }

  @Test
  public void test_getTransformationDepth() {
    when(fromText.getTransformationDepth()).thenReturn(42);
    assertThat(text.getTransformationDepth()).isEqualTo(43);
  }

  @Test
  public void test_getTransformedText() {
    assertThat(text.getTransformedText()).isSameAs(fromText);
  }

}
