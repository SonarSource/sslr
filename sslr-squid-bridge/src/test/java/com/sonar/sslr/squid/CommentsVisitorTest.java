/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import org.junit.Before;
import org.junit.Test;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceProject;
import org.sonar.squid.recognizer.CodeRecognizer;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.sonar.sslr.api.CommentAnalyser;
import com.sonar.sslr.api.Comments;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.*;

public class CommentsVisitorTest {

	private final SourceProject project = new SourceProject("");
  private final SourceFile file = new SourceFile("");
  private final SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(project);
  private final CodeRecognizer codeRecognizer = new DummyCodeRecognizer();

  @Before
  public void init() {
  	context.addSourceCode(file);
  }
  
  @Test
  public void shouldComputeCommentMeasures() {
  	assertThat(file.getNoSonarTagLines().size(), is(0));
  	assertThat(file.getInt(MyMetrics.COMMENTED_LINES_OF_CODE), is(0));
    assertThat(file.getInt(MyMetrics.COMMENT_LINES), is(0));
    assertThat(file.getInt(MyMetrics.BLANK_COMMENT_LINES), is(0));

    CommentsVisitor visitor = CommentsVisitor.builder(context).withBlankCommentMetric(MyMetrics.BLANK_COMMENT_LINES)
    																													.withCommentMetric(MyMetrics.COMMENT_LINES)
    																													.withCommentedLinesOfCodeMetric(codeRecognizer, MyMetrics.COMMENTED_LINES_OF_CODE)
    																													.withNoSonar(true)
    																													.build();

    ListMultimap<Integer, Token> comments = LinkedListMultimap.<Integer, Token> create();
    
    comments.put(1, new Token(GenericTokenType.COMMENT, "     ", 1, 0));
    comments.put(3, new Token(GenericTokenType.COMMENT, "  \r\n   ", 3, 0));
    comments.put(5, new Token(GenericTokenType.COMMENT, "  my comment  ", 5, 0));

    comments.put(8, new Token(GenericTokenType.COMMENT, "  hehe  ", 8, 0));
    comments.put(8, new Token(GenericTokenType.COMMENT, "  ", 8, 0));

    comments.put(10, new Token(GenericTokenType.COMMENT, "   ", 10, 0));
    comments.put(10, new Token(GenericTokenType.COMMENT, " test ", 10, 0));
    
    comments.put(15, new Token(GenericTokenType.COMMENT, " ", 15, 0));
    comments.put(15, new Token(GenericTokenType.COMMENT, "CODE", 15, 0));
    comments.put(15, new Token(GenericTokenType.COMMENT, "NOSONAR", 15, 0));
    comments.put(15, new Token(GenericTokenType.COMMENT, "hehe uhu", 15, 0));

    comments.put(18, new Token(GenericTokenType.COMMENT, "   ", 18, 0));
    comments.put(18, new Token(GenericTokenType.COMMENT, "test", 18, 0));
    comments.put(18, new Token(GenericTokenType.COMMENT, "CODE", 18, 0));
    
    comments.put(25, new Token(GenericTokenType.COMMENT, "test\n\n\nNOSONAR", 25, 0));

    context.setComments(new Comments(comments, new MyCommentAnayser()));

    visitor.visitFile(null);
    visitor.leaveFile(null);

  	assertThat(file.getNoSonarTagLines().size(), is(2));
  	assertThat(15, isIn(file.getNoSonarTagLines()));
  	assertThat(28, isIn(file.getNoSonarTagLines()));
  	
  	assertThat(file.getInt(MyMetrics.COMMENTED_LINES_OF_CODE), is(1));
  	
    assertThat(file.getInt(MyMetrics.COMMENT_LINES), is(4));
    assertThat(file.getInt(MyMetrics.BLANK_COMMENT_LINES), is(5));
  }

  private class MyCommentAnayser extends CommentAnalyser {

    public boolean isBlank(String commentLine) {
      for (int i = 0; i < commentLine.length(); i++) {
        if (Character.isLetterOrDigit(commentLine.charAt(i)))
          return false;
      }

      return true;
    }
  }
  
}
