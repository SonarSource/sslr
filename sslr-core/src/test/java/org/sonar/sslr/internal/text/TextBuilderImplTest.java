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

import org.junit.Test;
import org.sonar.sslr.text.Text;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TextBuilderImplTest {

  @Test
  public void should_start_with_empty_fragments() {
    assertThat(new TextBuilderImpl().getFragments()).isEmpty();
  }

  @Test
  public void should_append_fragments() {
    Text t1 = mock(Text.class);
    Text t2 = mock(Text.class);

    TextBuilderImpl textBuilder = new TextBuilderImpl();

    textBuilder.append(t1);
    assertThat(textBuilder.getFragments()).containsExactly(t1);

    textBuilder.append(t2);
    assertThat(textBuilder.getFragments()).containsExactly(t1, t2);
  }

  @Test
  public void should_return_same_instance_for_chaining_of_append() {
    TextBuilderImpl textBuilder = new TextBuilderImpl();

    assertThat(textBuilder.append(mock(Text.class))).isSameAs(textBuilder);
  }

}
