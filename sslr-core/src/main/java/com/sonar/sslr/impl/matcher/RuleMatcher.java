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
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNodeType;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.RuleRefExpression;

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public final class RuleMatcher extends MemoizedMatcher implements CompilableGrammarRule, ParsingExpression {

  private final GrammarRuleKey ruleKey;
  private final String name;
  private boolean recoveryRule = false;
  private AstNodeType astNodeType;
  private ParsingExpression expression;

  public RuleMatcher(GrammarRuleKey ruleKey, String name) {
    this.ruleKey = ruleKey;
    this.name = name;
  }

  /**
   * Should not be used directly, companion of {@link GrammarFunctions#enableMemoizationOfMatchesForAllRules(com.sonar.sslr.api.Grammar)}
   * and {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#buildWithMemoizationOfMatchesForAllRules()}.
   *
   * @since 1.14
   */
  public void memoizeMatches() {
    if (expression == null) {
      // skip empty rule
      return;
    }
    expression = (ParsingExpression) GrammarFunctions.Advanced.memoizeMatches(expression);
  }

  public void setNodeType(AstNodeType astNodeType) {
    this.astNodeType = astNodeType;
  }

  public String getName() {
    return name;
  }

  public void recoveryRule() {
    recoveryRule = true;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  public Instruction[] compile(CompilationHandler compiler) {
    return compiler.compile(new RuleRefExpression(getRuleKey()));
  }

  public GrammarRuleKey getRuleKey() {
    return ruleKey;
  }

  public ParsingExpression getExpression() {
    return expression;
  }

  public void setExpression(ParsingExpression expression) {
    this.expression = expression;
  }

}
