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
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.RuleRefExpression;

/**
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 */
public final class RuleDefinition implements Rule, AstNodeSkippingPolicy, GrammarRuleKey, CompilableGrammarRule, ParsingExpression {

  private final GrammarRuleKey ruleKey;
  private final String name;
  private ParsingExpression expression;
  private AstNodeType astNodeSkippingPolicy = NeverSkipFromAst.INSTANCE;

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

  public RuleDefinition is(Object... matchers) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(matchers);
    setExpression((ParsingExpression) GrammarFunctions.Standard.and(matchers));
    return this;
  }

  public RuleDefinition override(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    setExpression((ParsingExpression) GrammarFunctions.Standard.and(matchers));
    return this;
  }

  public void mock() {
    setExpression((ParsingExpression) Standard.firstOf(getName(), getName().toUpperCase()));
  }

  public void skip() {
    astNodeSkippingPolicy = AlwaysSkipFromAst.INSTANCE;
  }

  public void skipIf(AstNodeSkippingPolicy astNodeSkipPolicy) {
    astNodeSkippingPolicy = astNodeSkipPolicy;
  }

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

  public void recoveryRule() {
    // TODO
  }

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

  public GrammarRuleKey getRuleKey() {
    return ruleKey;
  }

  public ParsingExpression getExpression() {
    return expression;
  }

  public void setExpression(ParsingExpression expression) {
    this.expression = expression;
  }

  public Instruction[] compile(CompilationHandler compiler) {
    return compiler.compile(new RuleRefExpression(getRuleKey()));
  }

  @Override
  public String toString() {
    return getName();
  }

}
