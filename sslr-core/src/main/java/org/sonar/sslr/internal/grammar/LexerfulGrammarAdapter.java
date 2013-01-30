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
package org.sonar.sslr.internal.grammar;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.Collection;
import java.util.Map;

public class LexerfulGrammarAdapter extends LexerlessGrammar {

  private final Map<GrammarRuleKey, RuleDefinition> ruleMatchers;
  private final Rule rootRule;

  public LexerfulGrammarAdapter(Collection<LexerfulGrammarRuleDefinition> rules, GrammarRuleKey rootRuleKey, boolean enableMemoizationOfMathesForAllRules) {
    ImmutableMap.Builder<GrammarRuleKey, RuleDefinition> b = ImmutableMap.builder();
    for (LexerfulGrammarRuleDefinition definition : rules) {
      GrammarRuleKey ruleKey = definition.getRule();
      b.put(ruleKey, RuleDefinition.newRuleBuilder(definition.getName(), ruleKey));
    }
    this.ruleMatchers = b.build();

    for (LexerfulGrammarRuleDefinition definition : rules) {
      definition.build(this);
    }

    if (enableMemoizationOfMathesForAllRules) {
      for (RuleDefinition ruleDefinition : ruleMatchers.values()) {
        ruleDefinition.getRule().memoizeMatches();
      }
    }

    this.rootRule = ruleMatchers.get(rootRuleKey);
  }

  @Override
  public Rule getRootRule() {
    return rootRule;
  }

  @Override
  public Rule rule(GrammarRuleKey ruleKey) {
    return ruleMatchers.get(ruleKey);
  }

  @VisibleForTesting
  public Collection<GrammarRuleKey> ruleKeys() {
    return ruleMatchers.keySet();
  }

}
