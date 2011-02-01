/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.BasicDsl;

public class ConditionPrinterDsl extends BasicDsl {

  public Rule condition = new ConditionDsl().condition;

  public ConditionPrinterDsl() {
    statement.is(condition).plug(ConditionPrinter.class);
  }
}
