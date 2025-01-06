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
package org.sonar.sslr.internal.vm;

import org.sonar.sslr.grammar.GrammarRuleKey;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class MutableGrammarCompiler extends CompilationHandler {

  public static CompiledGrammar compile(CompilableGrammarRule rule) {
    return new MutableGrammarCompiler().doCompile(rule);
  }

  private final Queue<CompilableGrammarRule> compilationQueue = new ArrayDeque<>();
  private final Map<GrammarRuleKey, CompilableGrammarRule> matchers = new HashMap<>();
  private final Map<GrammarRuleKey, Integer> offsets = new HashMap<>();

  private CompiledGrammar doCompile(CompilableGrammarRule start) {
    List<Instruction> instructions = new ArrayList<>();

    // Compile

    compilationQueue.add(start);
    matchers.put(start.getRuleKey(), start);

    while (!compilationQueue.isEmpty()) {
      CompilableGrammarRule rule = compilationQueue.poll();
      GrammarRuleKey ruleKey = rule.getRuleKey();

      offsets.put(ruleKey, instructions.size());
      Instruction.addAll(instructions, compile(rule.getExpression()));
      instructions.add(Instruction.ret());
    }

    // Link

    Instruction[] result = instructions.toArray(new Instruction[instructions.size()]);
    for (int i = 0; i < result.length; i++) {
      Instruction instruction = result[i];
      if (instruction instanceof RuleRefExpression) {
        RuleRefExpression expression = (RuleRefExpression) instruction;
        GrammarRuleKey ruleKey = expression.getRuleKey();
        int offset = offsets.get(ruleKey);
        result[i] = Instruction.call(offset - i, matchers.get(ruleKey));
      }
    }

    return new CompiledGrammar(result, matchers, start.getRuleKey(), offsets.get(start.getRuleKey()));
  }

  @Override
  public Instruction[] compile(ParsingExpression expression) {
    if (expression instanceof CompilableGrammarRule) {
      CompilableGrammarRule rule = (CompilableGrammarRule) expression;
      if (!matchers.containsKey(rule.getRuleKey())) {
        compilationQueue.add(rule);
        matchers.put(rule.getRuleKey(), rule);
      }
      return rule.compile(this);
    } else {
      return expression.compile(this);
    }
  }

}
