/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.metrics;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.sonar.squid.api.SourceProject;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.squid.SquidAstVisitorContextImpl;
import com.sonar.sslr.squid.metrics.LinesVisitor;

public class LinesVisitorTest {

  private final SourceProject project = new SourceProject("myProject");
  private final SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(project);

  @Test
  public void shouldCompyteTheNumberOfLines() {
    assertThat(project.getInt(MyMetrics.LINES), is(0));

    LinesVisitor<Grammar> visitor = new LinesVisitor<Grammar>(MyMetrics.LINES);
    visitor.setContext(context);

    visitor.visitToken(new Token(GenericTokenType.EOF, "", 11, 0));
    assertThat(project.getInt(MyMetrics.LINES), is(11));
  }
}
