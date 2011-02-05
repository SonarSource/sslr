/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.CommandListDsl;

public class ConditionPrinterDsl extends CommandListDsl {

  public Rule condition = new ConditionDsl().condition;

  public ConditionPrinterDsl() {
    command.is(condition).plug(ConditionPrinter.class);
  }
}
