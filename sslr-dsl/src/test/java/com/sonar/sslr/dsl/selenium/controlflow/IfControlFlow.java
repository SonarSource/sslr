/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.selenium.controlflow;

import com.sonar.sslr.dsl.condition.Condition;

public class IfControlFlow {

  private Condition condition;

  public void setCondition(Condition condition) {
    this.condition = condition;
  }

  public boolean isTrue() {
    return condition.value();
  }
}
