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
package org.sonar.sslr.parser;

import org.junit.Test;
import org.sonar.sslr.internal.matchers.FirstOfMatcher;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.matchers.NothingMatcher;
import org.sonar.sslr.internal.matchers.OneOrMoreMatcher;
import org.sonar.sslr.internal.matchers.OptionalMatcher;
import org.sonar.sslr.internal.matchers.SequenceMatcher;
import org.sonar.sslr.internal.matchers.StringMatcher;
import org.sonar.sslr.internal.matchers.TestMatcher;
import org.sonar.sslr.internal.matchers.TestNotMatcher;
import org.sonar.sslr.internal.matchers.ZeroOrMoreMatcher;

import java.lang.reflect.Constructor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class GrammarFunctionsMigratorTest {

  @Test
  public void test() {
    Matcher subMatcher = mock(Matcher.class);
    assertThat(GrammarFunctionsMigrator.and(subMatcher)).isSameAs(subMatcher);
    assertThat(GrammarFunctionsMigrator.and(subMatcher, subMatcher)).isInstanceOf(SequenceMatcher.class);
    assertThat(GrammarFunctionsMigrator.and("foo")).isInstanceOf(StringMatcher.class);
    assertThat(GrammarFunctionsMigrator.and('f')).isInstanceOf(StringMatcher.class);

    assertThat(GrammarFunctionsMigrator.or(subMatcher)).isSameAs(subMatcher);
    assertThat(GrammarFunctionsMigrator.or(subMatcher, subMatcher)).isInstanceOf(FirstOfMatcher.class);

    assertThat(GrammarFunctionsMigrator.opt(subMatcher)).isInstanceOf(OptionalMatcher.class);

    assertThat(GrammarFunctionsMigrator.one2n(subMatcher)).isInstanceOf(OneOrMoreMatcher.class);

    assertThat(GrammarFunctionsMigrator.o2n(subMatcher)).isInstanceOf(ZeroOrMoreMatcher.class);

    assertThat(GrammarFunctionsMigrator.next(subMatcher)).isInstanceOf(TestMatcher.class);

    assertThat(GrammarFunctionsMigrator.not(subMatcher)).isInstanceOf(TestNotMatcher.class);

    assertThat(GrammarFunctionsMigrator.isFalse()).isInstanceOf(NothingMatcher.class);
  }

  @Test
  public void private_constructor() throws Exception {
    Constructor constructor = GrammarFunctionsMigrator.class.getDeclaredConstructor();
    assertThat(constructor.isAccessible()).isFalse();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
