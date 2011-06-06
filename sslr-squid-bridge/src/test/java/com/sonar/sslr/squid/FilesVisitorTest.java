/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceProject;

public class FilesVisitorTest {

  private Stack<SourceCode> sourceCodeStack = new Stack<SourceCode>();
  private SourceProject project = new SourceProject("myProject");

  @Before
  public void init() {
    sourceCodeStack.push(project);
  }

  @Test
  public void shouldPushSourceFileToTheStack() {
    assertThat(project.getInt(MyMetrics.FILES), is(0));

    FilesVisitor visitor = new FilesVisitor(MyMetrics.FILES);
    visitor.setFile(new File("SourceFile.c"));
    visitor.setSourceCodeStack(sourceCodeStack);

    visitor.visitFile(null);
    assertThat(sourceCodeStack.size(), is(2));
    assertThat(sourceCodeStack.peek().getInt(MyMetrics.FILES), is(1));
    assertThat(sourceCodeStack.peek(), is(instanceOf(SourceFile.class)));

    visitor.leaveFile(null);
    assertThat(sourceCodeStack.size(), is(1));
  }
}
