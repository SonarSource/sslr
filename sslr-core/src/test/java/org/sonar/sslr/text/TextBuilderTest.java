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
package org.sonar.sslr.text;

import org.junit.Test;
import org.sonar.sslr.internal.text.AbstractText;
import org.sonar.sslr.internal.text.CompositeText;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TextBuilderTest {

  @Test
  public void should_not_build_CompositeText() {
    Text text = when(mock(AbstractText.class).length()).thenReturn(1).getMock();
    Text result = TextBuilder.create()
        .append(text)
        .build();
    assertThat(result).isSameAs(text);
  }

  @Test
  public void should_build_CompositeText() {
    Text text1 = when(mock(AbstractText.class).length()).thenReturn(1).getMock();
    Text text2 = when(mock(AbstractText.class).length()).thenReturn(1).getMock();
    Text result = TextBuilder.create()
        .append(text1)
        .append(text2)
        .build();
    assertThat(result).isInstanceOf(CompositeText.class);
  }

  @Test
  public void should_build_empty_text() {
    Text result = TextBuilder.create()
        .build();
    assertThat(result.length()).isEqualTo(0);
  }

  @Test
  public void should_not_append_empty_text() {
    Text text1 = when(mock(AbstractText.class).length()).thenReturn(1).getMock();
    Text text2 = when(mock(AbstractText.class).length()).thenReturn(0).getMock();
    Text result = TextBuilder.create()
        .append(text1)
        .append(text2)
        .build();
    assertThat(result).isSameAs(text1);
  }

}
