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

import org.junit.Test;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceProject;

import com.sonar.sslr.api.Grammar;

public class FilesVisitorTest {

  private SourceProject project = new SourceProject("myProject");
  private SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(project);

  @Test
  public void shouldPushSourceFileToTheStack() {
    assertThat(project.getInt(MyMetrics.FILES), is(0));

    FilesVisitor<Grammar> visitor = new FilesVisitor<Grammar>(MyMetrics.FILES);
    visitor.setContext(context);
    
    context.setFile(new File("SourceFile.c"));

    visitor.visitFile(null);
    assertThat(context.peekSourceCode().getInt(MyMetrics.FILES), is(1));
    assertThat(context.peekSourceCode(), is(instanceOf(SourceFile.class)));

    visitor.leaveFile(null);
    assertThat(context.peekSourceCode(), is((SourceCode) project));
  }
}
