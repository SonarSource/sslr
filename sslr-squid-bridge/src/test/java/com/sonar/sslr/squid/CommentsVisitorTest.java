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

import org.junit.Test;
import org.sonar.squid.api.SourceProject;

import com.sonar.sslr.api.Comments;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

public class CommentsVisitorTest {

  private SourceProject project = new SourceProject("myProject");
  private SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(project);

  @Test
  public void shouldComputeCommentMeasures() {
    assertThat(project.getInt(MyMetrics.COMMENT_LINES), is(0));
    assertThat(project.getInt(MyMetrics.BLANK_COMMENT_LINES), is(0));

    CommentsVisitor visitor = new CommentsVisitor(context, MyMetrics.COMMENT_LINES, MyMetrics.BLANK_COMMENT_LINES);

    Map<Integer, Token> comments = new HashMap<Integer, Token>();
    comments.put(1, new Token(GenericTokenType.COMMENT, "     ", 1, 0));
    comments.put(3, new Token(GenericTokenType.COMMENT, "  \r\n   ", 3, 0));
    comments.put(5, new Token(GenericTokenType.COMMENT, "  my comment  ", 5, 0));
    context.setComments(new Comments(comments));

    visitor.visitFile(null);
    visitor.leaveFile(null);

    assertThat(project.getInt(MyMetrics.COMMENT_LINES), is(1));
    assertThat(project.getInt(MyMetrics.BLANK_COMMENT_LINES), is(3));
  }
}
