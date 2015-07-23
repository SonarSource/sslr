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
package org.sonar.sslr.internal.grammar;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MutableGrammarTest {

  @Test
  public void test() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    CompilableGrammarRule rule = mock(CompilableGrammarRule.class);
    GrammarRuleKey rootRuleKey = mock(GrammarRuleKey.class);
    CompilableGrammarRule rootRule = mock(CompilableGrammarRule.class);
    MutableGrammar grammar = new MutableGrammar(ImmutableMap.of(ruleKey, rule, rootRuleKey, rootRule), rootRuleKey);
    assertThat(grammar.rule(ruleKey)).isSameAs(rule);
    assertThat(grammar.getRootRule()).isSameAs(rootRule);
  }

}
