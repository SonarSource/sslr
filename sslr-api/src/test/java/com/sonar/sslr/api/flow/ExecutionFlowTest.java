/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static com.sonar.sslr.api.AstNodeUtils.createAstNode;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;

public class ExecutionFlowTest {

  ExecutionFlow flow = new ExecutionFlow();

  @Test
  public void shouldHandleLinkBetweenStatementAndAstNode() {
    AstNode stmtAstNode = createAstNode("myStatement");
    Statement stmt = new Statement(stmtAstNode);

    flow.add(stmt);

    assertThat(flow.getStatement(stmtAstNode), is(stmt));
  }

}
