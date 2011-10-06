/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public final class ChildSequenceMatcher extends StructuralUnitMatcher {

  private ChildMatcher childMatcher;
  private AfterMatcher afterMatcher;

  public void setChildMatcher(ChildMatcher childMatcher) {
    this.childMatcher = childMatcher;
  }

  public void setAfterMatcher(AfterMatcher afterMatcher) {
    this.afterMatcher = afterMatcher;
  }

  @Override
  public final AstNode match(AstNode node) {
    AstNode childNode = childMatcher.match(node);
    if (afterMatcher != null && afterMatcher.match(childNode) == null) {
      return null;
    }
    return childNode;
  }

}
