/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

import com.sonar.sslr.dsl.expression.AbstractExpression;

public abstract class ConditionOfCompositeExpression implements AbstractCondition {

  protected AbstractExpression firstExp;
  protected AbstractExpression secondExp;

  public void addArgument(AbstractExpression exp) {
    if (firstExp == null) {
      firstExp = exp;
    }
    secondExp = exp;
  }
}
