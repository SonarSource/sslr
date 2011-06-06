/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceProject;

import com.sonar.sslr.api.Comments;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;

public class CommentsVisitorTest {

  private Stack<SourceCode> sourceCodeStack = new Stack<SourceCode>();
  private SourceProject project = new SourceProject("myProject");

  @Before
  public void init() {
    sourceCodeStack.push(project);
  }

  @Test
  public void shouldComputeCommentMeasures() {
    assertThat(project.getInt(MyMetrics.COMMENT_LINES), is(0));
    assertThat(project.getInt(MyMetrics.BLANK_COMMENT_LINES), is(0));

    CommentsVisitor visitor = new CommentsVisitor(MyMetrics.COMMENT_LINES, MyMetrics.BLANK_COMMENT_LINES);
    visitor.setSourceCodeStack(sourceCodeStack);

    Map<Integer, Token> comments = new HashMap<Integer, Token>();
    comments.put(1, new Token(GenericTokenType.COMMENT, "     ", 1, 0));
    comments.put(3, new Token(GenericTokenType.COMMENT, "  \r\n   ", 3, 0));
    comments.put(5, new Token(GenericTokenType.COMMENT, "  my comment  ", 5, 0));
    visitor.setComments(new Comments(comments));

    visitor.visitFile(null);
    visitor.leaveFile(null);

    assertThat(project.getInt(MyMetrics.COMMENT_LINES), is(1));
    assertThat(project.getInt(MyMetrics.BLANK_COMMENT_LINES), is(3));
  }
}
