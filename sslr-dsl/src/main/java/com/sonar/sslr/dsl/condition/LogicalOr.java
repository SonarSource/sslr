/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

public class LogicalOr extends CompositeCondition {

  public boolean value() {
    return firstCondition.value() || secondCondition.value();
  }
}
