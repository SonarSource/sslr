/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

public abstract class CompositeCondition implements AbstractCondition {

  protected AbstractCondition firstCondition;
  protected AbstractCondition secondCondition;

  public void addCondition(AbstractCondition condition) {
    if (firstCondition == null) {
      firstCondition = condition;
    }
    secondCondition = condition;
  }

}
