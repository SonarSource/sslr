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

public class LinesVisitorTest {

  private Stack<SourceCode> sourceCodeStack = new Stack<SourceCode>();
  private SourceProject project = new SourceProject("myProject");

  @Before
  public void init() {
    sourceCodeStack.push(project);
  }

  @Test
  public void shouldCompyteTheNumberOfLines() {
    assertThat(project.getInt(MyMetrics.LINES), is(0));

    LinesVisitor visitor = new LinesVisitor(MyMetrics.LINES);
    visitor.setSourceCodeStack(sourceCodeStack);

    visitor.visitToken(new Token(GenericTokenType.EOF, "", 11, 0));
    assertThat(project.getInt(MyMetrics.LINES), is(11));
  }
}
