/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstListener;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.LeftRecursiveRule;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.NeverSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;

public class RuleDefinition implements Rule, LeftRecursiveRule, AstNodeSkippingPolicy {

  private RuleMatcher ruleMatcher;
  private Object adapter;
  private AstNodeType astNodeSkippingPolicy = new NeverSkipFromAst();

  private RuleDefinition() {
  }

  public static RuleDefinition newLeftRecursiveRuleBuilder(String ruleName) {
    RuleDefinition ruleBuilder = new RuleDefinition();
    ruleBuilder.setRuleMatcher(new LeftRecursiveRuleMatcher(ruleName));
    return ruleBuilder;
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
  public RuleDefinition is(Object... matchers) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleMatcher + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.and(matchers));
    return this;
  }

  /**
   * ${@inheritDoc}
   */
  public RuleDefinition override(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.and(matchers));
    return this;
  }

  public void mockUpperCase() {
    setMatcher(new TokenValueMatcher(ruleMatcher.getName().toUpperCase()));
  }

  public void mock() {
    setMatcher(new TokenValueMatcher(ruleMatcher.getName()));
  }

  public RuleDefinition isOr(Object... matchers) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleMatcher + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.or(matchers));
    return this;
  }

  public RuleDefinition or(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(GrammarFunctions.Standard.or(ruleMatcher.children[0], GrammarFunctions.Standard.and(matchers)));
    return this;
  }

  public RuleDefinition and(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.and(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(GrammarFunctions.Standard.and(ruleMatcher.children[0], GrammarFunctions.Standard.and(matchers)));
    return this;
  }

  public RuleDefinition orBefore(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(GrammarFunctions.Standard.or(GrammarFunctions.Standard.and(matchers), ruleMatcher.children[0]));
    return this;
  }

  public RuleDefinition skip() {
    astNodeSkippingPolicy = new AlwaysSkipFromAst();
    return this;
  }

  protected void setMatcher(Matcher matcher) {
    ruleMatcher.children = new Matcher[] { matcher };
  }

  public RuleDefinition setListener(AstListener listener) {
    ruleMatcher.setListener(listener);
    return this;
  }

  public RuleDefinition skipIf(AstNodeSkippingPolicy astNodeSkipPolicy) {
    astNodeSkippingPolicy = astNodeSkipPolicy;
    return this;
  }

  public RuleDefinition skipIfOneChild() {
    astNodeSkippingPolicy = new SkipFromAstIfOnlyOneChild();
    return this;
  }

  public RuleDefinition plug(Object adapter) {
    this.adapter = adapter;
    return this;
  }

  public void endParsing() {
    ruleMatcher.endParsing();
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

  public void recoveryRule() {
    ruleMatcher.recoveryRule();
  }

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
