/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
package org.sonar.sslr.internal.vm;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.matchers.Matcher;

import java.util.Map;

public class CompiledGrammar {

  private final Map<GrammarRuleKey, CompilableGrammarRule> rules;
  private final Instruction[] instructions;
  private final GrammarRuleKey rootRuleKey;
  private final int rootRuleOffset;

  public CompiledGrammar(Instruction[] instructions, Map<GrammarRuleKey, CompilableGrammarRule> rules, GrammarRuleKey rootRuleKey, int rootRuleOffset) {
    this.instructions = instructions;
    this.rules = rules;
    this.rootRuleKey = rootRuleKey;
    this.rootRuleOffset = rootRuleOffset;
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

  public int getRootRuleOffset() {
    return rootRuleOffset;
  }

}
