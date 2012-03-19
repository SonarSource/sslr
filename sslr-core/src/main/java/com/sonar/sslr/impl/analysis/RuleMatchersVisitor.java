/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.*;

/**
 * Visit all the matchers of a rule.
 * All rules, expect the entry one, encountered are not visited.
 */
public abstract class RuleMatchersVisitor extends MatcherVisitor<Object, Object> {

  public void process(TokenMatcher matcher) {
  }

  public void process(OrMatcher matcher) {
  }

  public void process(AndMatcher matcher) {
  }

  public void process(OptMatcher matcher) {
  }

  public void process(OneToNMatcher matcher) {
  }

  public void process(RuleMatcher matcher) {
  }

  @Override
  public final Object visit(TokenMatcher matcher, Object userObject) {
    process(matcher);
    visitAllButRuleMatchers(matcher, userObject);
    return null;
  }

  @Override
  public final Object visit(OrMatcher matcher, Object userObject) {
    process(matcher);
    visitAllButRuleMatchers(matcher, userObject);
    return null;
  }

  @Override
  public final Object visit(AndMatcher matcher, Object userObject) {
    process(matcher);
    visitAllButRuleMatchers(matcher, userObject);
    return null;
  }

  @Override
  public final Object visit(OptMatcher matcher, Object userObject) {
    process(matcher);
    visitAllButRuleMatchers(matcher, userObject);
    return null;
  }

  @Override
  public final Object visit(OneToNMatcher matcher, Object userObject) {
    process(matcher);
    visitAllButRuleMatchers(matcher, userObject);
    return null;
  }

  @Override
  public final Object visit(RuleMatcher matcher, Object userObject) {
    process(matcher);
    visitAllButRuleMatchers(matcher, userObject);
    return null;
  }

  private void visitAllButRuleMatchers(Matcher matcher, Object userObject) {
    for (Matcher child : matcher.children) {
      if (!(child instanceof RuleMatcher)) {
        visit(child, userObject);
      }
    }
  }

}
