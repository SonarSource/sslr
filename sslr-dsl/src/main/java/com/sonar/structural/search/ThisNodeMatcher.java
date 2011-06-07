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

public class ThisNodeMatcher extends CompositeMatcher {

  private Set<String> tokenValues = new HashSet<String>();
  private Set<String> nodeValues = new HashSet<String>();

  public void addTokenValue(Literal tokenValue) {
    tokenValues.add(tokenValue.toString());
  }

  public void addNodeName(String nodeValue) {
    nodeValues.add(nodeValue);
  }

  @Override
  public AstNode match(AstNode node) {
    if (tokenValues.isEmpty() && nodeValues.isEmpty()) {
      return matchChildMatcher(node);
    }
    if ( !tokenValues.isEmpty()) {
      for (String tokenValue : tokenValues) {
        if (node.getTokenValue().equals(tokenValue)) {
          return matchChildMatcher(node);
        }
      }
    }
    if ( !nodeValues.isEmpty()) {
      for (String nodeValue : nodeValues) {
        if (node.getName().equals(nodeValue)) {
          return matchChildMatcher(node);
        }
      }
    }
    return null;
  }

  private AstNode matchChildMatcher(AstNode node) {
    if (matcher == null) {
      return node;
    } else {
      return matcher.match(node);
    }
  }
}
