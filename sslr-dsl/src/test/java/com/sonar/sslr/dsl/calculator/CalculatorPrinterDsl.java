/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.calculator;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.CommandListDsl;

public class CalculatorPrinterDsl extends CommandListDsl {

  public Rule expression = new CalculatorDsl().expression;

  public CalculatorPrinterDsl() {
    command.is(expression).plug(CalculatorPrinter.class);
  }
}
