/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.impl.typed;

import com.google.common.base.Throwables;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.ParsingExpression;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class DelayedRuleInvocationExpression implements ParsingExpression {

  private static final Field DEFINITIONS_FIELD = ReflectionUtils.getField(LexerlessGrammarBuilder.class, "definitions");

  private final LexerlessGrammarBuilder b;
  private final GrammarBuilderInterceptor grammarBuilderInterceptor;
  private final Method method;
  private GrammarRuleKey ruleKey;

  public DelayedRuleInvocationExpression(LexerlessGrammarBuilder b, GrammarRuleKey ruleKey) {
    this.b = b;
    this.grammarBuilderInterceptor = null;
    this.method = null;
    this.ruleKey = ruleKey;
  }

  public DelayedRuleInvocationExpression(LexerlessGrammarBuilder b, GrammarBuilderInterceptor grammarBuilderInterceptor, Method method) {
    this.b = b;
    this.grammarBuilderInterceptor = grammarBuilderInterceptor;
    this.method = method;
    this.ruleKey = null;
  }

  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    if (ruleKey == null) {
      ruleKey = grammarBuilderInterceptor.ruleKeyForMethod(method);
      if (ruleKey == null) {
        throw new IllegalStateException("Cannot find the rule key corresponding to the invoked method: " + toString());
      }
    }

    try {
      // Ensure the MutableParsingRule is created in the definitions
      b.rule(ruleKey);
      return compiler.compile((MutableParsingRule) ((Map) DEFINITIONS_FIELD.get(b)).get(ruleKey));
    } catch (IllegalAccessException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public String toString() {
    if (ruleKey != null) {
      return ruleKey.toString();
    } else {
      return method.getName() + "()";
    }
  }

}
