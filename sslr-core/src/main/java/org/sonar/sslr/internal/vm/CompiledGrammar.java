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
package org.sonar.sslr.internal.vm;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;
import org.sonar.sslr.internal.matchers.Matcher;

import java.util.Map;

public class CompiledGrammar {

  private final Map<GrammarRuleKey, GrammarElementMatcher> rules;
  private final Instruction[] instructions;
  // TODO Is there more efficient way to store this information? I.e. without primitive wrappers.
  private final Map<GrammarRuleKey, Integer> offsets;
  private final GrammarRuleKey rootRuleKey;

  public CompiledGrammar(Instruction[] instructions, Map<GrammarRuleKey, Integer> offsets, Map<GrammarRuleKey, GrammarElementMatcher> rules, GrammarRuleKey rootRuleKey) {
    this.instructions = instructions;
    this.offsets = offsets;
    this.rules = rules;
    this.rootRuleKey = rootRuleKey;
  }

  public int getOffset(GrammarRuleKey ruleKey) {
    return offsets.get(ruleKey);
  }

  public Instruction[] getInstructions() {
    return instructions;
  }

  public Matcher getMatcher(GrammarRuleKey ruleKey) {
    return rules.get(ruleKey);
  }

  public GrammarRuleKey getRootRuleKey() {
    return rootRuleKey;
  }

}
