/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.bytecode.controlflow;

import com.sonar.sslr.dsl.bytecode.ProcedureDefinition;

public class Procedure implements ProcedureDefinition {

  private String procedureName;

  public void add(String procedureName) {
    this.procedureName = procedureName;
  }

  public String getProcedureName() {
    return procedureName;
  }
}
