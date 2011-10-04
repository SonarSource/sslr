/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public abstract class ParentNodeMatcher extends CompositeMatcher {

  protected String ruleName;
  private ChildNodeMatcher childNodeMatcher;

  public void addRuleName(String name) {
    ruleName = name;
  }

  public void addChildMatcher(ChildNodeMatcher childNodeMatcher) {
    this.childNodeMatcher = childNodeMatcher;
  }

  @Override
  public final AstNode match(AstNode node) {
    node = matcher.match(node);
    if (node == null) {
      return null;
    }
    AstNode parentNode = matchParents(node);
    if (parentNode == null) {
      return null;
    }
    if (childNodeMatcher != null && !childNodeMatcher.isMatching(parentNode)) {
      return null;
    }
    return parentNode;
  }

  public abstract AstNode matchParents(AstNode node);
}
