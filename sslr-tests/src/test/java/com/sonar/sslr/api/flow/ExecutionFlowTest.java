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
import org.junit.Test;
import org.mockito.InOrder;

import static com.sonar.sslr.test.lexer.MockHelper.mockAstNode;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ExecutionFlowTest {

  ExecutionFlowEngine flow = new ExecutionFlowEngine();

  @Test
  public void shouldHandleLinkBetweenStatementAndAstNode() {
    AstNode stmtAstNode = mockAstNode("myStatement");
    Statement stmt = new Statement(stmtAstNode);

    flow.add(stmt);

    assertThat(flow.getStatement(stmtAstNode)).isSameAs(stmt);
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
