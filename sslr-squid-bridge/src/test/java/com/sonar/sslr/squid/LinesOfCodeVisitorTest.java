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

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;

public class LinesOfCodeVisitorTest {

  private Stack<SourceCode> sourceCodeStack = new Stack<SourceCode>();
  private SourceProject project = new SourceProject("myProject");

  @Before
  public void init() {
    sourceCodeStack.push(project);
  }

  @Test
  public void shouldIncrementTheLinesOfCodeMeasure() {
    assertThat(project.getInt(MyMetrics.LINES_OF_CODE), is(0));

    LinesOfCodeVisitor visitor = new LinesOfCodeVisitor(MyMetrics.LINES_OF_CODE);
    visitor.setSourceCodeStack(sourceCodeStack);

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
