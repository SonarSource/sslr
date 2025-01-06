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
package org.sonar.sslr.internal.grammar;

import org.junit.Test;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;

import java.util.HashMap;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MutableGrammarTest {

  @Test
  public void test() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    CompilableGrammarRule rule = mock(CompilableGrammarRule.class);
    GrammarRuleKey rootRuleKey = mock(GrammarRuleKey.class);
    CompilableGrammarRule rootRule = mock(CompilableGrammarRule.class);
    HashMap<GrammarRuleKey, CompilableGrammarRule> rules = new HashMap<>();
    rules.put(ruleKey, rule);
    rules.put(rootRuleKey, rootRule);
    MutableGrammar grammar = new MutableGrammar(rules, rootRuleKey);
    assertThat(grammar.rule(ruleKey)).isSameAs(rule);
    assertThat(grammar.getRootRule()).isSameAs(rootRule);
  }

}
