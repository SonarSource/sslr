/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.selenium.controlflow;

import static com.sonar.sslr.impl.matcher.Matchers.o2n;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.BasicDsl;
import com.sonar.sslr.dsl.condition.ConditionDsl;

public class ControlFlowDsl extends BasicDsl {

  public Rule ifStmt;
  public Rule ping;
  public Rule condition = new ConditionDsl().condition;

  public ControlFlowDsl() {
    statement.isOr(ifStmt, ping);
    ifStmt.is("if", condition, o2n(statement), "endif").plug(IfControlFlow.class);
    ping.is("ping").plug(Ping.class);
  }
}
