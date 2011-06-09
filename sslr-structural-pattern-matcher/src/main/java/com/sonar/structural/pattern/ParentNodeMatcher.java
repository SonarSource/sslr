/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public abstract class ParentNodeMatcher extends CompositeMatcher {

  protected String ruleName;

  public void addRuleName(String name) {
    ruleName = name;
  }

  @Override
  public final AstNode match(AstNode node) {
    node = matcher.match(node);
    if (node != null) {
      return matchParents(node);
    } else {
      return null;
    }
  }

  public abstract AstNode matchParents(AstNode node);
}
