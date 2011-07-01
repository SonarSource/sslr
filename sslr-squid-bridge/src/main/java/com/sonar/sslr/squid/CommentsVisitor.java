/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.util.HashSet;

import org.sonar.squid.api.SourceFile;
import org.sonar.squid.measures.MetricDef;
import org.sonar.squid.recognizer.CodeRecognizer;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Parser;

/**
 * Visitor that computes the number of lines of comments and the number of empty lines of comments.
 */
public class CommentsVisitor extends SquidAstVisitor<Grammar> {

	private HashSet<Integer> noSonar;
	private HashSet<Integer> commentedLinesOfCode;
  private HashSet<Integer> comments;
  private HashSet<Integer> blankComments;
  
  private final SquidAstVisitorContext<? extends Grammar> context;
  private final boolean enableNoSonar;
  private final MetricDef commentMetric;
  private final MetricDef blankCommentMetric;
  private final CodeRecognizer codeRecognizer;
  private final MetricDef commentedLinesOfCodeMetric;
  
  private CommentsVisitor(CommentsVisitorBuilder builder) {
    this.context = builder.context;
    this.enableNoSonar = builder.enableNoSonar;
    this.commentMetric = builder.commentMetric;
    this.blankCommentMetric = builder.blankCommentMetric;
    this.codeRecognizer = builder.codeRecognizer;
    this.commentedLinesOfCodeMetric = builder.commentedLinesOfCodeMetric;
  }
  
  private void addNoSonar(int line) {
    /* Remove from lower priorities categories first */
  	commentedLinesOfCode.remove(line);
		comments.remove(line);
    blankComments.remove(line);

    noSonar.add(line);
  }
  
  private void addCommentedLineOfCode(int line) {
  	/* Mark the line only if it does not already have 1) no sonar */
  	if (!noSonar.contains(line)) {
	    /* Remove from lower priorities categories first */
  		comments.remove(line);
	    blankComments.remove(line);
	
	    commentedLinesOfCode.add(line);
  	}
  }

  private void addCommentLine(int line) {
  	/* Mark the line only if it does not already have 1) no sonar, 2) commented code */
  	if (!noSonar.contains(line) && !commentedLinesOfCode.contains(line)) {
  		/* Remove from lower priorities categories first */
	    blankComments.remove(line);
	
	    comments.add(line);
  	}
  }
  
  private void addBlankCommentLine(int line) {
    /* Mark the line only if it does not already have 1) no sonar, 2) commented code, or 3) a non-empty comment */
    if (!noSonar.contains(line) && !commentedLinesOfCode.contains(line) && !comments.contains(line)) {
      blankComments.add(line);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visitFile(AstNode astNode) {
  	noSonar = new HashSet<Integer>();
  	commentedLinesOfCode = new HashSet<Integer>();
    comments = new HashSet<Integer>();
    blankComments = new HashSet<Integer>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void leaveFile(AstNode astNode) {
  	if (astNode == null) return;
  	
    for (Token comment : context.getComments()) {
      String[] commentLines = comment.getValue().split("\n", -1);
      int line = comment.getLine();

      for (String commentLine : commentLines) {
      	if (enableNoSonar && commentLine.trim().startsWith("NOSONAR")) {
      		/* NOSONAR */
      		addNoSonar(line);
      	} else if (codeRecognizer != null && codeRecognizer.isLineOfCode(commentLine)) {
      		/* Commented line of code */
      		addCommentedLineOfCode(line);
      	} else if (blankCommentMetric != null && context.getComments().isBlank(commentLine)) {
      		/* Blank lines */
          addBlankCommentLine(line);
        } else if (commentMetric != null) {
        	/* Comment lines */
          addCommentLine(line);
        }

        line++;
      }
    }

    if (enableNoSonar) ((SourceFile)context.peekSourceCode()).addNoSonarTagLines(noSonar);
    if (commentedLinesOfCodeMetric != null) context.peekSourceCode().add(commentedLinesOfCodeMetric, commentedLinesOfCode.size());
    if (commentMetric != null) context.peekSourceCode().add(commentMetric, comments.size());
    if (blankCommentMetric != null) context.peekSourceCode().add(blankCommentMetric, blankComments.size());
  }
  
  public static CommentsVisitorBuilder builder(SquidAstVisitorContext<? extends Grammar> context) {
  	return new CommentsVisitorBuilder(context);
  }
  
  public final static class CommentsVisitorBuilder {
  	
  	private SquidAstVisitorContext<? extends Grammar> context;
    private boolean enableNoSonar;
    private MetricDef commentMetric;
    private MetricDef blankCommentMetric;
    private CodeRecognizer codeRecognizer;
    private MetricDef commentedLinesOfCodeMetric;
  	
  	private CommentsVisitorBuilder(SquidAstVisitorContext<? extends Grammar> context) {
  		this.context = context;
  	}
  	
    public CommentsVisitor build() {
      return new CommentsVisitor(this);
    }
    
    public CommentsVisitorBuilder withNoSonar(boolean enableNoSonar) {
    	this.enableNoSonar = enableNoSonar;
    	return this;
    }
    
    public CommentsVisitorBuilder withCommentMetric(MetricDef commentMetric) {
    	this.commentMetric = commentMetric;
    	return this;
    }
    
    public CommentsVisitorBuilder withBlankCommentMetric(MetricDef blankCommentMetric) {
    	this.blankCommentMetric = blankCommentMetric;
    	return this;
    }
    
    public CommentsVisitorBuilder withCommentedLinesOfCodeMetric(CodeRecognizer codeRecognizer, MetricDef commentedLinesOfCodeMetric) {
    	this.codeRecognizer = codeRecognizer;
    	this.commentedLinesOfCodeMetric = commentedLinesOfCodeMetric;
    	return this;
    }
  	
  }

}
