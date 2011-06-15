/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.util.HashSet;

import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

/**
 * Visitor that computes the number of lines of comments and the number of empty lines of comments.
 */
public class CommentsVisitor extends SquidAstVisitor<Grammar> {

  private HashSet<Integer> comments;
  private HashSet<Integer> blankComments;
  private final SquidAstVisitorContext<? extends Grammar> context;

  private MetricDef commentMetric;
  private MetricDef blankCommentMetric;

  public CommentsVisitor(SquidAstVisitorContext<? extends Grammar> context, MetricDef commentMetric, MetricDef blankCommentMetric) {
    this.context = context;
    this.commentMetric = commentMetric;
    this.blankCommentMetric = blankCommentMetric;
  }

  private void addBlankCommentLine(int line) {
    /* Mark the line as empty comment only if it does not already have a non-empty comment */
    if ( !comments.contains(line)) {
      blankComments.add(line);
    }
  }

  private void addCommentLine(int line) {
    /* If there is an empty comment at that line, then remove it first */
    if (blankComments.contains(line)) {
      blankComments.remove(line);
    }

    comments.add(line);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visitFile(AstNode astNode) {
    comments = new HashSet<Integer>();
    blankComments = new HashSet<Integer>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void leaveFile(AstNode astNode) {
    for (Token comment : context.getComments()) {
      String[] commentLines = comment.getValue().split("\n", -1);
      int line = comment.getLine();

      for (String commentLine : commentLines) {
        if (context.getComments().isBlank(commentLine)) {
          addBlankCommentLine(line);
        } else {
          addCommentLine(line);
        }

        line++;
      }
    }

    context.peekSourceCode().add(commentMetric, comments.size());
    context.peekSourceCode().add(blankCommentMetric, blankComments.size());
  }

}
