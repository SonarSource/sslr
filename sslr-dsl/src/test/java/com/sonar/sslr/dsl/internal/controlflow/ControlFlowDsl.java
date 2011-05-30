/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal.controlflow;

import static com.sonar.sslr.api.GrammarFunctions.Standard.o2n;
import static com.sonar.sslr.dsl.DslTokenType.INTEGER;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.CommandListDsl;
import com.sonar.sslr.dsl.adapter.ExitFlowAdapter;
import com.sonar.sslr.dsl.condition.ConditionDsl;

public class ControlFlowDsl extends CommandListDsl {

  public Rule ifBlock;
  public Rule loop;
  public Rule ping;
  public Rule exit;
  public Rule condition = new ConditionDsl().condition;

  public ControlFlowDsl() {
    command.isOr(ifBlock, ping, loop, exit);
    ifBlock.is("if", condition, o2n(command), "endif").plug(If.class);
    loop.is("do", INTEGER, "times", o2n(command), "enddo").plug(Loop.class);
    ping.is("ping").plug(Ping.class);
    exit.is("exit").plug(ExitFlowAdapter.class);
  }
}
