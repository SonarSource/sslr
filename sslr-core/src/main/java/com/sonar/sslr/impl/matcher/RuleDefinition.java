/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
  private Object adapter;
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

  /**
   * ${@inheritDoc}
   */
  @Override
  public RuleDefinition is(Object... matchers) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleMatcher + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.and(matchers));
    return this;
  }

  /**
   * ${@inheritDoc}
   */
  @Override
  public RuleDefinition override(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.and(matchers));
    return this;
  }

  @Override
  public void mock() {
    setMatcher(Standard.or(ruleMatcher.getName(), ruleMatcher.getName().toUpperCase()));
  }

  @Override
  public RuleDefinition isOr(Object... matchers) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleMatcher + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.or(matchers));
    return this;
  }

  @Override
  public RuleDefinition or(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(GrammarFunctions.Standard.or(ruleMatcher.children[0], GrammarFunctions.Standard.and(matchers)));
    return this;
  }

  @Override
  public RuleDefinition and(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.and(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(GrammarFunctions.Standard.and(ruleMatcher.children[0], GrammarFunctions.Standard.and(matchers)));
    return this;
  }

  @Override
  public RuleDefinition orBefore(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(GrammarFunctions.Standard.or(GrammarFunctions.Standard.and(matchers), ruleMatcher.children[0]));
    return this;
  }

  @Override
  public void skip() {
    astNodeSkippingPolicy = new AlwaysSkipFromAst();
  }

  protected void setMatcher(Matcher matcher) {
    ruleMatcher.children = new Matcher[] {matcher};
  }

  @Override
  public void skipIf(AstNodeSkippingPolicy astNodeSkipPolicy) {
    astNodeSkippingPolicy = astNodeSkipPolicy;
  }

  @Override
  public void skipIfOneChild() {
    astNodeSkippingPolicy = new SkipFromAstIfOnlyOneChild();
  }

  @Override
  public void plug(Object adapter) {
    this.adapter = adapter;
  }

  private void throwExceptionIfRuleAlreadyDefined(String exceptionMessage) {
    if (ruleMatcher.children.length != 0) {
      throw new IllegalStateException(exceptionMessage);
    }
  }

  private void throwExceptionIfRuleNotAlreadyDefined(String exceptionMessage) {
    if (ruleMatcher.children.length == 0) {
      throw new IllegalStateException(exceptionMessage);
    }
  }

  private void throwExceptionIfEmptyListOfMatchers(Object[] matchers) {
    if (matchers.length == 0) {
      throw new IllegalStateException("The rule '" + ruleMatcher + "' should at least contains one matcher.");
    }
  }

  public Object getAdapter() {
    return adapter;
  }

  @Override
  public void recoveryRule() {
    ruleMatcher.recoveryRule();
  }

  @Override
  public String toString() {
    return ruleMatcher.getName();
  }

  @Override
  public boolean hasToBeSkippedFromAst(AstNode node) {
    if (AstNodeSkippingPolicy.class.isAssignableFrom(astNodeSkippingPolicy.getClass())) {
      return ((AstNodeSkippingPolicy) astNodeSkippingPolicy).hasToBeSkippedFromAst(node);
    }
    return false;
  }
}
