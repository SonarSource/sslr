/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.bytecode.controlflow;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.bytecode.ExitFlowInstruction;
import com.sonar.sslr.dsl.condition.ConditionDsl;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.dsl.DefaultDslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DefaultDslTokenType.WORD;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;

public class ControlFlowDsl extends Grammar {

  public Rule translationUnit;
  public Rule command;
  public Rule ifBlock;
  public Rule loop;
  public Rule loopNumber;
  public Rule callProcedure;
  public Rule procedureDefinition;
  public Rule procedureCall;
  public Rule procedureName;
  public Rule ping;
  public Rule exit;
  public Rule condition = new ConditionDsl().condition;

  public ControlFlowDsl() {
    translationUnit.is(o2n(command), o2n(procedureDefinition), EOF);
    command.isOr(ifBlock, ping, loop, procedureCall, exit);
    ifBlock.is("if", condition, o2n(command), "endif").plug(If.class);
    loop.is("do", loopNumber, "times", o2n(command), "enddo").plug(Loop.class);
    ping.is("ping").plug(Ping.class);
    procedureCall.is("call", procedureName).plug(ProcedureCall.class);
    exit.is("exit").plug(ExitFlowInstruction.class);
    procedureDefinition.is("procedure", procedureName, o2n(command), "end").plug(Procedure.class);

    procedureName.is(WORD).plug(String.class);
    loopNumber.is(INTEGER).plug(Integer.class);
  }

  @Override
  public Rule getRootRule() {
    return translationUnit;
  }
}
