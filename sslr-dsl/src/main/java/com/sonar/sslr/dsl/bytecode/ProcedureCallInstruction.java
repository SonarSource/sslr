/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.bytecode;

public interface ProcedureCallInstruction extends ControlFlowInstruction {

  public String getProcedureNameToCall();

}
