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

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

public class LinesVisitorTest {

  private SourceProject project = new SourceProject("myProject");
  private SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(project);

  @Test
  public void shouldCompyteTheNumberOfLines() {
    assertThat(project.getInt(MyMetrics.LINES), is(0));

    LinesVisitor visitor = new LinesVisitor(context, MyMetrics.LINES);

    visitor.visitToken(new Token(GenericTokenType.EOF, "", 11, 0));
    assertThat(project.getInt(MyMetrics.LINES), is(11));
  }
}
