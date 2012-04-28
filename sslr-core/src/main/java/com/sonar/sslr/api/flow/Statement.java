/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public class Statement {

  private final AstNode astNode;
  private FlowHandler flowHandler;
  private Statement nextStmt;
  private Statement previousStmt;

  public Statement(AstNode stmtAstNode) {
    this.astNode = stmtAstNode;
  }

  public AstNode getAstNode() {
    return astNode;
  }

  @Override
  public String toString() {
    return "Statement (" + astNode + ")";
  }

  public void setFlowHandler(FlowHandler flowHandler) {
    this.flowHandler = flowHandler;
  }

  public FlowHandler getFlowHandler() {
    return flowHandler;
  }

  public boolean hasFlowHandler() {
    return flowHandler != null;
  }

  public void setNext(Statement nextStmt) {
    this.nextStmt = nextStmt;
    if (nextStmt != null) {
      nextStmt.previousStmt = this;
    }
  }

  public Statement getNext() {
    return nextStmt;
  }

  public Statement getPrevious() {
    return previousStmt;
  }

  public boolean hasNext() {
    return nextStmt != null;
  }

  public boolean hasPrevious() {
    return previousStmt != null;
  }
}
