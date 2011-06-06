/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceProject;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleDefinition;

public class ComplexityVisitorTest {

  private Rule ifStmt = RuleDefinition.newRuleBuilder("ifStmt");
  private Stack<SourceCode> sourceCodeStack = new Stack<SourceCode>();
  private SourceProject project = new SourceProject("myProject");

  @Before
  public void init() {
    sourceCodeStack.push(project);
  }

  @Test
  public void shouldIncrementTheComplexityWhenVisitingANode() {
    assertThat(project.getInt(MyMetrics.COMPLEXITY), is(0));

    ComplexityVisitor visitor = ComplexityVisitor.builder().setMetricDef(MyMetrics.COMPLEXITY).subscribeTo(ifStmt).build();
    visitor.setSourceCodeStack(sourceCodeStack);

    visitor.visitNode(null);
    assertThat(project.getInt(MyMetrics.COMPLEXITY), is(1));

    visitor.visitNode(null);
    assertThat(project.getInt(MyMetrics.COMPLEXITY), is(2));
  }
}
