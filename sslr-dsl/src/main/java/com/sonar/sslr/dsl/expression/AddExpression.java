/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.expression;

public class AddExpression extends CompositeExpression {

  public double value() {
    return firstExp.value() + secondExp.value();
  }

}
