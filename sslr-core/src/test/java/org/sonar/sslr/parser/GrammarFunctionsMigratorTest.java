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
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.NextExpression;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.NothingExpression;
import org.sonar.sslr.internal.vm.OneOrMoreExpression;
import org.sonar.sslr.internal.vm.OptionalExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.StringExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;

import java.lang.reflect.Constructor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class GrammarFunctionsMigratorTest {

  @Test
  public void test() {
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);

    assertThat(GrammarFunctionsMigrator.and(e1)).isSameAs(e1);
    assertThat(GrammarFunctionsMigrator.and(e1, e2)).isInstanceOf(SequenceExpression.class);
    assertThat(GrammarFunctionsMigrator.and("foo")).isInstanceOf(StringExpression.class);
    assertThat(GrammarFunctionsMigrator.and('f')).isInstanceOf(StringExpression.class);

    assertThat(GrammarFunctionsMigrator.or(e1)).isSameAs(e1);
    assertThat(GrammarFunctionsMigrator.or(e1, e2)).isInstanceOf(FirstOfExpression.class);

    assertThat(GrammarFunctionsMigrator.opt(e1)).isInstanceOf(OptionalExpression.class);

    assertThat(GrammarFunctionsMigrator.one2n(e1)).isInstanceOf(OneOrMoreExpression.class);

    assertThat(GrammarFunctionsMigrator.o2n(e1)).isInstanceOf(ZeroOrMoreExpression.class);

    assertThat(GrammarFunctionsMigrator.next(e1)).isInstanceOf(NextExpression.class);

    assertThat(GrammarFunctionsMigrator.not(e1)).isInstanceOf(NextNotExpression.class);

    assertThat(GrammarFunctionsMigrator.isFalse()).isInstanceOf(NothingExpression.class);
  }

  @Test
  public void private_constructor() throws Exception {
    Constructor constructor = GrammarFunctionsMigrator.class.getDeclaredConstructor();
    assertThat(constructor.isAccessible()).isFalse();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
