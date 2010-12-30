/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public class ConditionalPathType extends PathType {

  private final AstNode condition;
  private final boolean expectedValue;

  public ConditionalPathType(AstNode condition, boolean expectedValue) {
    this.condition = condition;
    this.expectedValue = expectedValue;
  }

  public AstNode getCondition() {
    return condition;
  }

  public boolean getExpectedResult() {
    return expectedValue;
  }

}
