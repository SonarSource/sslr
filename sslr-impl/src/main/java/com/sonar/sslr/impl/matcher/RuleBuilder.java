/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstListener;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.LeftRecursiveRule;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;

public class RuleBuilder implements Rule, LeftRecursiveRule {

  private RuleMatcher ruleMatcher;

  public RuleBuilder(String name, boolean leftRecursiveRule) {
    if (leftRecursiveRule) {
      ruleMatcher = new LeftRecursiveRuleMatcher(name);
    } else {
      ruleMatcher = new RuleMatcher(name);
    }
  }

  public RuleBuilder(RuleMatcher ruleMatcher) {
    this.ruleMatcher = ruleMatcher;
  }

  public RuleMatcher getRule() {
    return ruleMatcher;
  }

  public void replaceRuleMatcher(RuleMatcher ruleMatcher) {
    this.ruleMatcher = ruleMatcher;
  }

  /**
   * ${@inheritDoc}
   */
  public RuleBuilder is(Object... matchers) {
    throwExceptionIfRuleAlreadyDefined("The rule '" + ruleMatcher + "' has already been defined somewhere in the grammar.");
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(CfgFunctions.Standard.and(matchers));
    return this;
  }

  /**
   * ${@inheritDoc}
   */
  public RuleBuilder override(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    setMatcher(CfgFunctions.Standard.and(matchers));
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
    setMatcher(CfgFunctions.Standard.or(matchers));
    return this;
  }

  public RuleBuilder or(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(CfgFunctions.Standard.or(ruleMatcher.children[0], CfgFunctions.Standard.and(matchers)));
    return this;
  }

  public RuleBuilder and(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.and(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(CfgFunctions.Standard.and(ruleMatcher.children[0], CfgFunctions.Standard.and(matchers)));
    return this;
  }

  public RuleBuilder orBefore(Object... matchers) {
    throwExceptionIfEmptyListOfMatchers(matchers);
    throwExceptionIfRuleNotAlreadyDefined("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    setMatcher(CfgFunctions.Standard.or(CfgFunctions.Standard.and(matchers), ruleMatcher.children[0]));
    return this;
  }

  public RuleBuilder skip() {
    ruleMatcher.skipIf(new AlwaysSkipFromAst());
    return this;
  }

  protected void setMatcher(Matcher matcher) {
    ruleMatcher.children = new Matcher[] { matcher };
  }

  public RuleBuilder setListener(AstListener listener) {
    ruleMatcher.setListener(listener);
    return this;
  }

  public RuleBuilder skipIf(AstNodeType astNodeSkipPolicy) {
    ruleMatcher.skipIf(astNodeSkipPolicy);
    return this;
  }

  public RuleBuilder skipIfOneChild() {
    ruleMatcher.skipIf(new SkipFromAstIfOnlyOneChild());
    return this;
  }

  public RuleBuilder plug(Class adapterClass) {
    ruleMatcher.plug(adapterClass);
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

  public void recoveryRule() {
    ruleMatcher.recoveryRule();
  }

  public String toString() {
    return ruleMatcher.getName();
  }
}
