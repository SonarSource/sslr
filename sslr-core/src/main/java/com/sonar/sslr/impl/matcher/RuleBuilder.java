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
import com.sonar.sslr.api.GrammarFunctions;
import com.sonar.sslr.api.LeftRecursiveRule;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.NeverSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;

public class RuleBuilder implements Rule, LeftRecursiveRule, AstNodeSkippingPolicy {

  private RuleMatcher ruleMatcher;
  private Class adapterClass;
  private AstNodeType astNodeSkippingPolicy = new NeverSkipFromAst();

  private RuleBuilder() {
  }

  public static RuleBuilder newLeftRecursiveRuleBuilder(String ruleName) {
    RuleBuilder ruleBuilder = new RuleBuilder();
    ruleBuilder.setRuleMatcher(new LeftRecursiveRuleMatcher(ruleName));
    return ruleBuilder;
  }

  public static RuleBuilder newRuleBuilder(String ruleName) {
    RuleBuilder ruleBuilder = new RuleBuilder();
    ruleBuilder.setRuleMatcher(new RuleMatcher(ruleName));
    return ruleBuilder;
  }

  public static RuleBuilder newRuleBuilder(RuleMatcher ruleMatcher) {
    RuleBuilder ruleBuilder = new RuleBuilder();
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
  public RuleBuilder is(Object... matchers) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleMatcher + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.and(matchers));
    return this;
  }

  /**
   * ${@inheritDoc}
   */
  public RuleBuilder override(Object... matchers) {
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

  public RuleBuilder isOr(Object... matchers) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleMatcher + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(GrammarFunctions.Standard.or(matchers));
    return this;
  }

  public RuleBuilder or(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(GrammarFunctions.Standard.or(ruleMatcher.children[0], GrammarFunctions.Standard.and(matchers)));
    return this;
  }

  public RuleBuilder and(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.and(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(GrammarFunctions.Standard.and(ruleMatcher.children[0], GrammarFunctions.Standard.and(matchers)));
    return this;
  }

  public RuleBuilder orBefore(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(GrammarFunctions.Standard.or(GrammarFunctions.Standard.and(matchers), ruleMatcher.children[0]));
    return this;
  }

  public RuleBuilder skip() {
    astNodeSkippingPolicy = new AlwaysSkipFromAst();
    return this;
  }

  protected void setMatcher(Matcher matcher) {
    ruleMatcher.children = new Matcher[] { matcher };
  }

  public RuleBuilder setListener(AstListener listener) {
    ruleMatcher.setListener(listener);
    return this;
  }

  public RuleBuilder skipIf(AstNodeSkippingPolicy astNodeSkipPolicy) {
    astNodeSkippingPolicy = astNodeSkipPolicy;
    return this;
  }

  public RuleBuilder skipIfOneChild() {
    astNodeSkippingPolicy = new SkipFromAstIfOnlyOneChild();
    return this;
  }

  public RuleBuilder plug(Class adapterClass) {
    this.adapterClass = adapterClass;
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

  public Class getAdapter() {
    return adapterClass;
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
