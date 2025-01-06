/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.tests;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.fest.assertions.Assertions.assertThat;

public class AssertionsTest {

  @Test
  public void test() {
    assertThat(Assertions.assertThat((Parser) null)).isInstanceOf(ParserAssert.class);
    assertThat(Assertions.assertThat((Rule) null)).isInstanceOf(RuleAssert.class);
  }

  @Test
  public void private_constructor() throws Exception {
    Constructor constructor = Assertions.class.getDeclaredConstructor();
    assertThat(constructor.isAccessible()).isFalse();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
