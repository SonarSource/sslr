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

public class LinesOfCodeVisitorTest {

  private SourceProject project = new SourceProject("myProject");
  private SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(project);

  @Test
  public void shouldIncrementTheLinesOfCodeMeasure() {
    assertThat(project.getInt(MyMetrics.LINES_OF_CODE), is(0));

    LinesOfCodeVisitor visitor = new LinesOfCodeVisitor(context, MyMetrics.LINES_OF_CODE);

    visitor.visitToken(new Token(GenericTokenType.EOF, "", 1, 0));
    assertThat(project.getInt(MyMetrics.LINES_OF_CODE), is(0));

    visitor.visitToken(new Token(GenericTokenType.IDENTIFIER, "value", 2, 0));
    assertThat(project.getInt(MyMetrics.LINES_OF_CODE), is(1));

    visitor.visitToken(new Token(GenericTokenType.IDENTIFIER, "value", 2, 0));
    assertThat(project.getInt(MyMetrics.LINES_OF_CODE), is(1));

    visitor.visitToken(new Token(GenericTokenType.IDENTIFIER, "value", 3, 0));
    assertThat(project.getInt(MyMetrics.LINES_OF_CODE), is(2));
  }
}
