/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.calculator;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

public class CalculatorPrinterDsl extends Grammar {

  public Rule translationUnit;
  public Rule command;
  public Rule expression = new CalculatorDsl().expression;

  public CalculatorPrinterDsl() {
    translationUnit.is(o2n(command), EOF);
    command.is(expression).plug(CalculatorPrinter.class);
  }

  @Override
  public Rule getRootRule() {
    return translationUnit;
  }
}
