/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.expression;

public class ExpressionPrinter {

  private AbstractExpression value;
  private StringBuilder output;

  public void setOutput(StringBuilder output) {
    this.output = output;
  }

  public void setValue(Expression value) {
    this.value = value;
  }

  public void execute() {
    output.append(value.value());
  }

}
