/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
