/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.InOrder;

import com.sonar.sslr.api.AstNode;

public class ExecutionFlowTest {

  ExecutionFlowEngine flow = new ExecutionFlowEngine();

  @Test
  public void shouldHandleLinkBetweenStatementAndAstNode() {
    AstNode stmtAstNode = mockAstNode("myStatement");
    Statement stmt = new Statement(stmtAstNode);

    flow.add(stmt);

    assertThat(flow.getStatement(stmtAstNode), is(stmt));
  }

  @Test
  public void shouldCallStartAndEndOnVisitors() {
    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);
    flow.visitFlow(new Statement(null), visitor);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).start();
    inOrder.verify(visitor, times(1)).endPath(org.mockito.Matchers.any(Branch.class));
    inOrder.verify(visitor, times(1)).stop();
  }

  @Test
  public void shouldCallVisitStatementOnVisitors() {
    Statement stmt1 = new Statement(mockAstNode("myStmt1"));
    Statement stmt2 = new Statement(mockAstNode("myStmt2"));
    stmt1.setNext(stmt2);
    Statement stmt3 = new Statement(mockAstNode("myStmt3"));
    stmt2.setNext(stmt3);

    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);
    flow.visitFlow(stmt1, visitor);

    verify(visitor, times(3)).visitStatement(org.mockito.Matchers.any(Statement.class), org.mockito.Matchers.any(Branch.class));
    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visitStatement(eq(stmt1), org.mockito.Matchers.any(Branch.class));
    inOrder.verify(visitor, times(1)).visitStatement(eq(stmt2), org.mockito.Matchers.any(Branch.class));
    inOrder.verify(visitor, times(1)).visitStatement(eq(stmt3), org.mockito.Matchers.any(Branch.class));
  }

  @Test
  public void shouldCallProcessFlowOnFlowHandler() {
    Statement stmt = new Statement(mockAstNode("myStmt"));
    FlowHandler flowHandler = mock(FlowHandler.class);
    stmt.setFlowHandler(flowHandler);

    flow.visitFlow(stmt);
    flow.start();

    verify(flowHandler, times(1)).processFlow(org.mockito.Matchers.any(ExecutionFlowEngine.class));
  }

  @Test
  public void shouldStopFlowExploration() {
    Statement stmt1 = new Statement(mockAstNode("myStmt1"));
    FlowHandler flowHandler = new FlowHandler() {

      @Override
      public Statement processFlow(ExecutionFlowEngine flowExplorer) {
        throw new StopFlowExplorationSignal();

      }
    };

    stmt1.setFlowHandler(flowHandler);
    Statement stmt2 = new Statement(mockAstNode("myStmt2"));
    stmt1.setNext(stmt2);

    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);

    flow.visitFlow(stmt1, visitor);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visitStatement(org.mockito.Matchers.any(Statement.class), org.mockito.Matchers.any(Branch.class));
  }

  @Test
  public void shouldEndPathExploration() {
    Statement stmt1 = new Statement(mockAstNode("myStmt1"));
    FlowHandler flowHandler = new FlowHandler() {

      @Override
      public Statement processFlow(ExecutionFlowEngine flowExplorer) {
        throw new StopPathExplorationSignal();

      }
    };

    stmt1.setFlowHandler(flowHandler);
    Statement stmt2 = new Statement(mockAstNode("myStmt2"));
    stmt1.setNext(stmt2);

    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);

    flow.visitFlow(stmt1, visitor);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visitStatement(org.mockito.Matchers.any(Statement.class), org.mockito.Matchers.any(Branch.class));
    inOrder.verify(visitor, times(1)).endPath(org.mockito.Matchers.any(Branch.class));
  }
}
