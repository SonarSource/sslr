/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

public abstract class ParentNodeMatcher extends CompositeMatcher {

  protected String ruleName;

  public void addRuleName(String name) {
    ruleName = name;
  }
}
