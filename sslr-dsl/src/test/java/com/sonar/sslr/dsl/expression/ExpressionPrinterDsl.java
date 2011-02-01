/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.expression;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.BasicDsl;

public class ExpressionPrinterDsl extends BasicDsl {

  public Rule expression = new ExpressionDsl().expression;

  public ExpressionPrinterDsl() {
    statement.is(expression).plug(ExpressionPrinter.class);
  }
}
