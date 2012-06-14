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
