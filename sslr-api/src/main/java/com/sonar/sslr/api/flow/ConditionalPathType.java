/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public class ConditionalPathType extends PathType {

  private final AstNode condition;

  public ConditionalPathType(AstNode condition) {
    this.condition = condition;
  }

  public AstNode getCondition() {
    return condition;
  }

}
