/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sonar.squid.api.SourceProject;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleDefinition;

public class CounterVisitorTest {

  private Rule ifStmt = RuleDefinition.newRuleBuilder("ifStmt");
  private SourceProject project = new SourceProject("myProject");
  private SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(project);

  @Test
  public void shouldIncrementTheComplexityWhenVisitingANode() {
    assertThat(project.getInt(MyMetrics.COMPLEXITY), is(0));

    CounterVisitor<Grammar> visitor = CounterVisitor.<Grammar>builder().setMetricDef(MyMetrics.COMPLEXITY).subscribeTo(ifStmt).build();
    visitor.setContext(context);

    visitor.visitNode(null);
    assertThat(project.getInt(MyMetrics.COMPLEXITY), is(1));

    visitor.visitNode(null);
    assertThat(project.getInt(MyMetrics.COMPLEXITY), is(2));
  }
}
