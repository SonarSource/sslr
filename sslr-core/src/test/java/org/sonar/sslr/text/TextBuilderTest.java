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
package org.sonar.sslr.text;

import org.junit.Test;
import org.sonar.sslr.internal.text.AbstractText;
import org.sonar.sslr.internal.text.CompositeText;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TextBuilderTest {

  @Test
  public void should_not_build_CompositeText() {
    AbstractText text = mock(AbstractText.class);
    Text result = TextBuilder.create()
        .append(text)
        .build();
    assertThat(result).isSameAs(text);
  }

  @Test
  public void should_build_CompositeText() {
    AbstractText text1 = mock(AbstractText.class);
    AbstractText text2 = mock(AbstractText.class);
    Text result = TextBuilder.create()
        .append(text1)
        .append(text2)
        .build();
    assertThat(result).isInstanceOf(CompositeText.class);
  }

}
