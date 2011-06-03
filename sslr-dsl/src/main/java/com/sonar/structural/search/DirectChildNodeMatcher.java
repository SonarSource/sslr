/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import com.sonar.sslr.api.AstNode;

public class DirectChildNodeMatcher extends ChildNodeMatcher {

  @Override
  public AstNode match(AstNode node) {
    return null;
  }
}
