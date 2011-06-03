/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import java.util.HashSet;
import java.util.Set;

import com.sonar.sslr.api.AstNode;

public class ThisNodeMatcher extends StructuralSearchMatcher {

  private Set<String> tokenValues = new HashSet<String>();

  public void addTokenValue(String value) {
    tokenValues.add(value.substring(1, value.length() - 1));
  }

  @Override
  public AstNode match(AstNode node) {
    return null;
  }
}
