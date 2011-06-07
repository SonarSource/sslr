/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import java.util.HashSet;
import java.util.Set;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.Literal;

public class BeforeMatcher {

  private Set<String> tokenValues = new HashSet<String>();
  private Set<String> nodeValues = new HashSet<String>();
  private BeforeMatcher previousBeforeMatcher;

  public void addTokenValue(Literal tokenValue) {
    tokenValues.add(tokenValue.toString());
  }

  public void addNodeName(String nodeValue) {
    nodeValues.add(nodeValue);
  }

  public void setBeforeMatcher(BeforeMatcher beforeMatcher) {
    this.previousBeforeMatcher = beforeMatcher;
  }

  public AstNode matchBefore(AstNode node) {
    return null;
  }
}
