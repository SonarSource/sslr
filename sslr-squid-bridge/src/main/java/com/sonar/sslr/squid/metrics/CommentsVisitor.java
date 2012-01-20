/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.metrics;

import java.util.HashSet;
import java.util.Set;

import org.sonar.squid.api.SourceFile;
import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.squid.SquidAstVisitor;

/**
 * Visitor that computes the number of lines of comments and the number of empty lines of comments.
 */
public final class CommentsVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> implements AstAndTokenVisitor {

  private Set<Integer> noSonar;
  private Set<Integer> comments;
  private Set<Integer> blankComments;
  private Token firstToken;

  private final boolean enableNoSonar;
  private final MetricDef commentMetric;
  private final MetricDef blankCommentMetric;
  private final boolean ignoreHeaderComments;

  private CommentsVisitor(CommentsVisitorBuilder<GRAMMAR> builder) {
    this.enableNoSonar = builder.enableNoSonar;
    this.commentMetric = builder.commentMetric;
    this.blankCommentMetric = builder.blankCommentMetric;
    this.ignoreHeaderComments = builder.ignoreHeaderComments;
  }

  private void addNoSonar(int line) {
    /* Remove from lower priorities categories first */
    comments.remove(line);
    blankComments.remove(line);

    noSonar.add(line);
  }

  private void addCommentLine(int line) {
    /* Mark the line only if it does not already have 1) no sonar */
    if ( !noSonar.contains(line)) {
      /* Remove from lower priorities categories first */
      blankComments.remove(line);

      comments.add(line);
    }
  }

  private void addBlankCommentLine(int line) {
    /* Mark the line only if it does not already have 1) no sonar, or 2) a non-empty comment */
    if ( !noSonar.contains(line) && !comments.contains(line)) {
      blankComments.add(line);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visitFile(AstNode astNode) {
    noSonar = new HashSet<Integer>();
    comments = new HashSet<Integer>();
    blankComments = new HashSet<Integer>();
    firstToken = null;
  }

  public void visitToken(Token token) {
    if (ignoreHeaderComments && firstToken == null) {
      firstToken = token;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void leaveFile(AstNode astNode) {
    if (getContext().getComments() != null) {
      for (Token commentToken : getContext().getComments()) {
        if (ignoreHeaderComments && firstToken != null && isTokenBefore(commentToken, firstToken)) {
          continue;
        }

        String[] commentLines = getContext().getCommentAnalyser().getContents(commentToken.getOriginalValue()).split("(\r)?\n|\r", -1);
        int line = commentToken.getLine();

        for (String commentLine : commentLines) {
          if (enableNoSonar && commentLine.contains("NOSONAR")) {
            /* NOSONAR */
            addNoSonar(line);
          } else if (blankCommentMetric != null && getContext().getCommentAnalyser().isBlank(commentLine)) {
            /* Blank lines */
            addBlankCommentLine(line);
          } else if (commentMetric != null) {
            /* Comment lines */
            addCommentLine(line);
          }

          line++;
        }
      }
    }

    if (enableNoSonar) {
      ((SourceFile) getContext().peekSourceCode()).addNoSonarTagLines(noSonar);
    }
    if (commentMetric != null) {
      getContext().peekSourceCode().add(commentMetric, comments.size());
    }
    if (blankCommentMetric != null) {
      getContext().peekSourceCode().add(blankCommentMetric, blankComments.size());
    }
  }

  private boolean isTokenBefore(Token a, Token b) {
    return a.getLine() < b.getLine() || a.getLine() == b.getLine() && a.getColumn() < b.getColumn();
  }

  public static <GRAMMAR extends Grammar> CommentsVisitorBuilder<GRAMMAR> builder() {
    return new CommentsVisitorBuilder<GRAMMAR>();
  }

  public final static class CommentsVisitorBuilder<GRAMMAR extends Grammar> {

    private boolean enableNoSonar = false;
    private MetricDef commentMetric;
    private MetricDef blankCommentMetric;
    private boolean ignoreHeaderComments = false;

    private CommentsVisitorBuilder() {
    }

    public CommentsVisitor<GRAMMAR> build() {
      return new CommentsVisitor<GRAMMAR>(this);
    }

    public CommentsVisitorBuilder<GRAMMAR> withNoSonar(boolean enableNoSonar) {
      this.enableNoSonar = enableNoSonar;
      return this;
    }

    public CommentsVisitorBuilder<GRAMMAR> withCommentMetric(MetricDef commentMetric) {
      this.commentMetric = commentMetric;
      return this;
    }

    public CommentsVisitorBuilder<GRAMMAR> withBlankCommentMetric(MetricDef blankCommentMetric) {
      this.blankCommentMetric = blankCommentMetric;
      return this;
    }

    public CommentsVisitorBuilder<GRAMMAR> withIgnoreHeaderComment(boolean ignoreHeaderComments) {
      this.ignoreHeaderComments = ignoreHeaderComments;
      return this;
    }

  }

}
