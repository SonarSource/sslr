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
package org.sonar.sslr.internal.matchers;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.fest.assertions.Assertions.assertThat;

public class TextUtilsTest {

  @Test
  public void should_escape() {
    assertThat(TextUtils.escape('\r')).isEqualTo("\\r");
    assertThat(TextUtils.escape('\n')).isEqualTo("\\n");
    assertThat(TextUtils.escape('\f')).isEqualTo("\\f");
    assertThat(TextUtils.escape('\t')).isEqualTo("\\t");
    assertThat(TextUtils.escape('"')).isEqualTo("\\\"");
    assertThat(TextUtils.escape('\\')).isEqualTo("\\");
  }

  @Test
  public void should_trim_trailing_line_separator() {
    assertThat(TextUtils.trimTrailingLineSeparatorFrom("\r\n")).isEqualTo("");
    assertThat(TextUtils.trimTrailingLineSeparatorFrom("\r\nfoo\r\n")).isEqualTo("\r\nfoo");
  }

  @Test
  public void private_constructor() throws Exception {
    Constructor constructor = TextUtils.class.getDeclaredConstructor();
    assertThat(constructor.isAccessible()).isFalse();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
