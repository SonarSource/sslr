/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.matcher;

import com.sonar.sslr.api.AstNode;

public abstract class ChildNodeMatcher extends CompositeMatcher {

  protected String ruleName;

  public void addRuleName(String name) {
    ruleName = name;
  }

  @Override
  public final AstNode match(AstNode node) {
    node = matchChildren(node);
    if (node != null) {
      if (matcher != null) {
        return matcher.match(node);
      } else {
        return node;
      }
    }
    return null;
  }

  protected abstract AstNode matchChildren(AstNode node);
}
