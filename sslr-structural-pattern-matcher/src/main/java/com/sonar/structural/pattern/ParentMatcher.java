/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public final class ParentMatcher extends CompositeMatcher {

  protected String rule;
  private ChildSequenceMatcher childSequenceMather;

  public void setRule(String rule) {
    this.rule = rule;
  }

  public void addChildSequenceMatcher(ChildSequenceMatcher childSequenceMather) {
    this.childSequenceMather = childSequenceMather;
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
    if (childSequenceMather != null && !childSequenceMather.isMatching(parentNode)) {
      return null;
    }
    return parentNode;
  }

  public AstNode matchParents(AstNode node) {
    if (node.getParent() != null) {
      if (node.getParent().getName().equals(rule)) {
        return node.getParent();
      } else {
        return matchParents(node.getParent());
      }
    }
    return null;
  }
}
