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

public final class RuleDefinition implements Rule, AstNodeSkippingPolicy {

  private RuleMatcher ruleMatcher;
  private AstNodeType astNodeSkippingPolicy = new NeverSkipFromAst();

  private RuleDefinition() {
  }

  public static RuleDefinition newRuleBuilder(String ruleName) {
    RuleDefinition ruleBuilder = new RuleDefinition();
    ruleBuilder.setRuleMatcher(new RuleMatcher(ruleName));
    return ruleBuilder;
  }

  public static RuleDefinition newRuleBuilder(RuleMatcher ruleMatcher) {
    RuleDefinition ruleBuilder = new RuleDefinition();
    ruleBuilder.setRuleMatcher(ruleMatcher);
    return ruleBuilder;
  }

  public RuleMatcher getRule() {
    return ruleMatcher;
  }

  public void setRuleMatcher(RuleMatcher ruleMatcher) {
    this.ruleMatcher = ruleMatcher;
    ruleMatcher.setNodeType(this);
  }

  public RuleDefinition is(Object... matchers) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleMatcher + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.and(matchers));
    return this;
  }

  public RuleDefinition override(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.and(matchers));
    return this;
  }

  public void mock() {
    setMatcher(Standard.firstOf(ruleMatcher.getName(), ruleMatcher.getName().toUpperCase()));
  }

  public void skip() {
    astNodeSkippingPolicy = new AlwaysSkipFromAst();
  }

  protected void setMatcher(Matcher matcher) {
    ruleMatcher.children = new Matcher[] {matcher};
  }

  public void skipIf(AstNodeSkippingPolicy astNodeSkipPolicy) {
    astNodeSkippingPolicy = astNodeSkipPolicy;
  }

  public void skipIfOneChild() {
    astNodeSkippingPolicy = new SkipFromAstIfOnlyOneChild();
  }

  private void throwExceptionIfRuleAlreadyDefined(String exceptionMessage) {
    if (ruleMatcher.children.length != 0) {
      throw new IllegalStateException(exceptionMessage);
    }
  }

  private void throwExceptionIfEmptyListOfMatchers(Object[] matchers) {
    if (matchers.length == 0) {
      throw new IllegalStateException("The rule '" + ruleMatcher + "' should at least contains one matcher.");
    }
  }

  public void recoveryRule() {
    ruleMatcher.recoveryRule();
  }

  @Override
  public String toString() {
    return ruleMatcher.getName();
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    if (AstNodeSkippingPolicy.class.isAssignableFrom(astNodeSkippingPolicy.getClass())) {
      return ((AstNodeSkippingPolicy) astNodeSkippingPolicy).hasToBeSkippedFromAst(node);
    }
    return false;
  }
}
