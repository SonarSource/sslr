/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

public class StructuralSearchPattern {

  private StructuralSearchMatcher matcher;

  public void add(StructuralSearchMatcher matcher) {
    this.matcher = matcher;
  }

  public StructuralSearchMatcher getMatcher() {
    return matcher;
  }

}
