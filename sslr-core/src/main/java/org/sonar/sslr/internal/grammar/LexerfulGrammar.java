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

import com.google.common.collect.ImmutableMap;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.sonar.sslr.grammar.Grammar;
import org.sonar.sslr.grammar.GrammarRule;
import org.sonar.sslr.grammar.LexerfulGrammarBuilder;
import org.sonar.sslr.grammar.LexerfulGrammarRuleDefinition;

import java.util.Map;

public class LexerfulGrammar implements Grammar {

  private final Map<GrammarRule, RuleDefinition> ruleMatchers;

  public LexerfulGrammar(LexerfulGrammarBuilder builder) {
    ImmutableMap.Builder<GrammarRule, RuleDefinition> b = ImmutableMap.builder();

    for (LexerfulGrammarRuleDefinition definition : builder.rules()) {
      b.put(definition.getRule(), RuleDefinition.newRuleBuilder(definition.getName()));
    }
    this.ruleMatchers = b.build();

    for (LexerfulGrammarRuleDefinition definition : builder.rules()) {
      definition.build(this);
    }
  }

  public Rule rule(GrammarRule rule) {
    return ruleMatchers.get(rule);
  }

}
