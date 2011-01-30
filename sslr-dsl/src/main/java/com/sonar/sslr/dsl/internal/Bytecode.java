/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sonar.sslr.dsl.DslException;

public class Bytecode {

  private List<Object> instructions = new ArrayList<Object>();

  public void execute() {
    for (Object stmt : instructions) {
      try {
        Method method = stmt.getClass().getMethod("execute");
        method.invoke(stmt);
      } catch (Exception e) {
        throw new DslException("Unable to call " + stmt.getClass().getName() + ".execute() method", e);
      }
    }
  }

  public void addInstruction(Object instruction) {
    if (instruction != null & hasExecuteMethod(instruction)) {
      instructions.add(instruction);
    }
  }

  private boolean hasExecuteMethod(Object instruction) {
    try {
      instruction.getClass().getMethod("execute");
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
