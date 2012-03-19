/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.*;

public abstract class MatcherVisitor<R, U> {

  public MatcherVisitor() {
  }

  public final R visit(Matcher matcher, U userObject) {
    if (matcher instanceof TokenMatcher) {
      return visit((TokenMatcher) matcher, userObject);
    } else if (matcher instanceof OrMatcher) {
      return visit((OrMatcher) matcher, userObject);
    } else if (matcher instanceof AndMatcher) {
      return visit((AndMatcher) matcher, userObject);
    } else if (matcher instanceof OptMatcher) {
      return visit((OptMatcher) matcher, userObject);
    } else if (matcher instanceof OneToNMatcher) {
      return visit((OneToNMatcher) matcher, userObject);
    } else if (matcher instanceof RuleMatcher) {
      return visit((RuleMatcher) matcher, userObject);
    } else {
      throw new IllegalArgumentException("The matcher type \"" + matcher.getClass().getSimpleName() + "\" is not supported");
    }
  }

  public abstract R visit(TokenMatcher matcher, U userObject);

  public abstract R visit(OrMatcher matcher, U userObject);

  public abstract R visit(AndMatcher matcher, U userObject);

  public abstract R visit(OptMatcher matcher, U userObject);

  public abstract R visit(OneToNMatcher matcher, U userObject);

  public abstract R visit(RuleMatcher matcher, U userObject);

}
