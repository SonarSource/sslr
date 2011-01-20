/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static com.sonar.sslr.api.AstNodeUtils.createAstNode;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.InOrder;

public class ExecutionFlowExplorerTest {

  ExecutionFlow flow = new ExecutionFlow();

  @Test
  public void shouldCallStartAndEndOnVisitors() {
    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);
    flow.visitFlow(new Statement(null), visitor);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).start();
    inOrder.verify(visitor, times(1)).endPath();
    inOrder.verify(visitor, times(1)).stop();
  }

  @Test
  public void shouldCallVisitStatementOnVisitors() {
    Statement stmt1 = new Statement(createAstNode("myStmt1"));
    Statement stmt2 = new Statement(createAstNode("myStmt2"));
    stmt1.setNext(stmt2);
    Statement stmt3 = new Statement(createAstNode("myStmt3"));
    stmt2.setNext(stmt3);

    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);
    flow.visitFlow(stmt1, visitor);

    verify(visitor, times(3)).visitStatement(any(Statement.class));
    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visitStatement(stmt1);
    inOrder.verify(visitor, times(1)).visitStatement(stmt2);
    inOrder.verify(visitor, times(1)).visitStatement(stmt3);
  }

  @Test
  public void shouldCallProcessFlowOnFlowHandler() {
    Statement stmt = new Statement(createAstNode("myStmt"));
    FlowHandler flowHandler = mock(FlowHandler.class);
    stmt.setFlowHandler(flowHandler);

    flow.visitFlow(stmt);

    verify(flowHandler, times(1)).processFlow(any(ExecutionFlowExplorer.class));
  }

  @Test
  public void shouldStopFlowExploration() {
    Statement stmt1 = new Statement(createAstNode("myStmt1"));
    FlowHandler flowHandler = new FlowHandler() {

      @Override
      public void processFlow(ExecutionFlowExplorer flowExplorer) {
        throw new StopFlowExplorationSignal();

      }
    };

    stmt1.setFlowHandler(flowHandler);
    Statement stmt2 = new Statement(createAstNode("myStmt2"));
    stmt1.setNext(stmt2);

    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);

    flow.visitFlow(stmt1, visitor);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visitStatement(any(Statement.class));
    inOrder.verify(visitor, never()).endPath();
  }

  @Test
  public void shouldEndPathExploration() {
    Statement stmt1 = new Statement(createAstNode("myStmt1"));
    FlowHandler flowHandler = new FlowHandler() {

      @Override
      public void processFlow(ExecutionFlowExplorer flowExplorer) {
        throw new EndPathSignal();

      }
    };

    stmt1.setFlowHandler(flowHandler);
    Statement stmt2 = new Statement(createAstNode("myStmt2"));
    stmt1.setNext(stmt2);

    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);

    flow.visitFlow(stmt1, visitor);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visitStatement(any(Statement.class));
    inOrder.verify(visitor, times(1)).endPath();
  }
}
