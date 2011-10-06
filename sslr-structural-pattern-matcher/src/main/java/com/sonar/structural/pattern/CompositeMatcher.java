/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

public abstract class CompositeMatcher extends StructuralUnitMatcher {

  protected StructuralUnitMatcher matcher;

  public void add(StructuralUnitMatcher matcher) {
    this.matcher = matcher;
  }
}
