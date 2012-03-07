/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.structural.pattern;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.Literal;

public final class ThisNodeMatcher extends CompositeMatcher {

  private final Set<String> tokenValues = new HashSet<String>();
  private final Set<String> rules = new HashSet<String>();

  public void addTokenValue(Literal tokenValue) {
    tokenValues.add(StringUtils.remove(tokenValue.toString(), '\\'));
  }

  public void addRule(String rule) {
    rules.add(rule);
  }

  @Override
  public AstNode match(AstNode node) {
    if (tokenValues.isEmpty() && rules.isEmpty()) {
      return matchChildMatcher(node);
    }
    if ( !tokenValues.isEmpty()) {
      for (String tokenValue : tokenValues) {
        if (node.getTokenValue().equals(tokenValue)) {
          return matchChildMatcher(node);
        }
      }
    }
    if ( !rules.isEmpty()) {
      for (String nodeValue : rules) {
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
