/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import com.sonar.sslr.api.AstNode;

public class IndirectParentNodeMatcher extends ParentNodeMatcher {

  @Override
  public AstNode matchParents(AstNode node) {
    if (node.getParent() != null) {
      if (node.getParent().getName().equals(ruleName)) {
        return node.getParent();
      } else {
        return matchParents(node.getParent());
      }
    }
    return null;
  }
}
