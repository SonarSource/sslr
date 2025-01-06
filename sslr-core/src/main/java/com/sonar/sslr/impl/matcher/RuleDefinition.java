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
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.NeverSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;
import com.sonar.sslr.impl.matcher.GrammarFunctions.Standard;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.MemoParsingExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.RuleRefExpression;

/**
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 */
public class RuleDefinition implements Rule, AstNodeSkippingPolicy, GrammarRuleKey, CompilableGrammarRule, MemoParsingExpression {

  private final GrammarRuleKey ruleKey;
  private final String name;
  private ParsingExpression expression;
  private AstNodeType astNodeSkippingPolicy = NeverSkipFromAst.INSTANCE;
  private boolean memoize = false;

  public RuleDefinition(String name) {
    this.ruleKey = this;
    this.name = name;
  }

  public RuleDefinition(GrammarRuleKey ruleKey) {
    this.ruleKey = ruleKey;
    this.name = ruleKey.toString();
  }

  public String getName() {
    return name;
  }

  @Override
  public RuleDefinition is(Object... e) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(e);
    setExpression(GrammarFunctions.convertToSingleExpression(e));
    return this;
  }

  @Override
  public RuleDefinition override(Object... e) {
    throwExceptionIfEmptyListOfMatchers(e);
    setExpression(GrammarFunctions.convertToSingleExpression(e));
    return this;
  }

  @Override
  public void mock() {
    setExpression((ParsingExpression) Standard.firstOf(getName(), getName().toUpperCase()));
  }

  @Override
  public void skip() {
    astNodeSkippingPolicy = AlwaysSkipFromAst.INSTANCE;
  }

  @Override
  public void skipIfOneChild() {
    astNodeSkippingPolicy = SkipFromAstIfOnlyOneChild.INSTANCE;
  }

  private void throwExceptionIfRuleAlreadyDefined(String exceptionMessage) {
    if (getExpression() != null) {
      throw new IllegalStateException(exceptionMessage);
    }
  }

  private void throwExceptionIfEmptyListOfMatchers(Object[] matchers) {
    if (matchers.length == 0) {
      throw new IllegalStateException("The rule '" + ruleKey + "' should at least contains one matcher.");
    }
  }

  @Override
  public boolean hasToBeSkippedFromAst(AstNode node) {
    if (AstNodeSkippingPolicy.class.isAssignableFrom(astNodeSkippingPolicy.getClass())) {
      return ((AstNodeSkippingPolicy) astNodeSkippingPolicy).hasToBeSkippedFromAst(node);
    }
    return false;
  }

  /**
   * @since 1.18
   */
  public AstNodeType getRealAstNodeType() {
    return ruleKey;
  }

  @Override
  public GrammarRuleKey getRuleKey() {
    return ruleKey;
  }

  @Override
  public ParsingExpression getExpression() {
    return expression;
  }

  @Override
  public void setExpression(ParsingExpression expression) {
    this.expression = expression;
  }

  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    return compiler.compile(new RuleRefExpression(getRuleKey()));
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean shouldMemoize() {
    return memoize;
  }

  public void enableMemoization() {
    memoize = true;
  }

}
