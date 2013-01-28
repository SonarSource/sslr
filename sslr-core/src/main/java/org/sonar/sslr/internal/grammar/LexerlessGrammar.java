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

import org.sonar.sslr.grammar.Grammar;

import com.google.common.collect.ImmutableMap;
import com.sonar.sslr.api.Rule;
import org.sonar.sslr.grammar.GrammarRule;
import org.sonar.sslr.grammar.LexerlessGrammarRuleDefinition;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;

import java.util.Map;

public class LexerlessGrammar implements Grammar {

  private final Map<GrammarRule, GrammarElementMatcher> ruleMatchers;

  public LexerlessGrammar(LexerlessGrammarBuilder builder) {
    ImmutableMap.Builder<GrammarRule, GrammarElementMatcher> b = ImmutableMap.builder();

    for (LexerlessGrammarRuleDefinition definition : builder.rules()) {
      b.put(definition.getRule(), new GrammarElementMatcher(definition.getName()));
    }
    this.ruleMatchers = b.build();

    for (LexerlessGrammarRuleDefinition definition : builder.rules()) {
      definition.build(this);
    }
  }

  public Rule rule(GrammarRule rule) {
    return ruleMatchers.get(rule);
  }

}
