/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.LinkedList;
import java.util.List;

public class Block {

  private final List<Statement> stmts = new LinkedList<Statement>();

  void addStatement(Statement stmt) {
    stmts.add(stmt);
  }

  public List<Statement> getStatements() {
    return stmts;
  }

  public int indexOf(Statement stmt) {
    int index = 0;
    for (Statement statement : stmts) {
      if (statement == stmt) {
        return index;
      }
      index++;
    }
    return -1;
  }
}
