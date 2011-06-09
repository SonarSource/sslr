/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public class DirectParentNodeMatcher extends ParentNodeMatcher {

  @Override
  public AstNode matchParents(AstNode node) {
    if (node.getParent() != null && node.getParent().getName().equals(ruleName)) {
      return node.getParent();
    }
    return null;
  }
}
