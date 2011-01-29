/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import java.util.List;

public class Bytecode {

  private List<Object> stmts;

  public Bytecode(List<Object> stmts) {
    this.stmts = stmts;
  }

  public void execute() {
    for (Object stmt : stmts) {
      ReflexionUtil.call(stmt, "execute");
    }
  }
}
