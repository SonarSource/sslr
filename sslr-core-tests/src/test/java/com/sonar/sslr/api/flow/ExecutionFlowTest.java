/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static com.sonar.sslr.api.AstNodeUtils.createAstNode;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.InOrder;

import com.sonar.sslr.api.AstNode;

public class ExecutionFlowTest {

  ExecutionFlowEngine flow = new ExecutionFlowEngine();

  @Test
  public void shouldHandleLinkBetweenStatementAndAstNode() {
    AstNode stmtAstNode = createAstNode("myStatement");
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
    inOrder.verify(visitor, times(1)).endPath(any(Branch.class));
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

    verify(visitor, times(3)).visitStatement(any(Statement.class), any(Branch.class));
    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visitStatement(eq(stmt1), any(Branch.class));
    inOrder.verify(visitor, times(1)).visitStatement(eq(stmt2), any(Branch.class));
    inOrder.verify(visitor, times(1)).visitStatement(eq(stmt3), any(Branch.class));
  }

  @Test
  public void shouldCallProcessFlowOnFlowHandler() {
    Statement stmt = new Statement(createAstNode("myStmt"));
    FlowHandler flowHandler = mock(FlowHandler.class);
    stmt.setFlowHandler(flowHandler);

    flow.visitFlow(stmt);
    flow.start();

    verify(flowHandler, times(1)).processFlow(any(ExecutionFlowEngine.class));
  }

  @Test
  public void shouldStopFlowExploration() {
    Statement stmt1 = new Statement(createAstNode("myStmt1"));
    FlowHandler flowHandler = new FlowHandler() {

      @Override
      public Statement processFlow(ExecutionFlowEngine flowExplorer) {
        throw new StopFlowExplorationSignal();

      }
    };

    stmt1.setFlowHandler(flowHandler);
    Statement stmt2 = new Statement(createAstNode("myStmt2"));
    stmt1.setNext(stmt2);

    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);

    flow.visitFlow(stmt1, visitor);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visitStatement(any(Statement.class), any(Branch.class));
  }

  @Test
  public void shouldEndPathExploration() {
    Statement stmt1 = new Statement(createAstNode("myStmt1"));
    FlowHandler flowHandler = new FlowHandler() {

      @Override
      public Statement processFlow(ExecutionFlowEngine flowExplorer) {
        throw new StopPathExplorationSignal();

      }
    };

    stmt1.setFlowHandler(flowHandler);
    Statement stmt2 = new Statement(createAstNode("myStmt2"));
    stmt1.setNext(stmt2);

    ExecutionFlowVisitor visitor = mock(ExecutionFlowVisitor.class);

    flow.visitFlow(stmt1, visitor);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visitStatement(any(Statement.class), any(Branch.class));
    inOrder.verify(visitor, times(1)).endPath(any(Branch.class));
  }
}
