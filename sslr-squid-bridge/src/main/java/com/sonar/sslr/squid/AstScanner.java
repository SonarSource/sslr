/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.squid.api.AnalysisException;
import org.sonar.squid.api.SourceCodeSearchEngine;
import org.sonar.squid.indexer.SquidIndex;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AuditListener;
import com.sonar.sslr.api.CommentAnalyser;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ast.AstWalker;
import com.sonar.sslr.impl.events.ExtendedStackTrace;

public final class AstScanner<GRAMMAR extends Grammar> {

  private static final Logger LOG = LoggerFactory.getLogger(AstScanner.class);
  private final SquidAstVisitorContextImpl<GRAMMAR> context;
  private final Parser<GRAMMAR> parser;
  private final List<SquidAstVisitor<GRAMMAR>> visitors;
  private final List<AuditListener> auditListeners;
  private final SquidIndex indexer = new SquidIndex();
  private final CommentAnalyser commentAnalyser;
  private Parser<GRAMMAR> debugParser;
  private ExtendedStackTrace extendedStackTrace;

  private AstScanner(Builder<GRAMMAR> builder) {
    this.parser = builder.parser;
    this.visitors = new ArrayList<SquidAstVisitor<GRAMMAR>>(builder.visitors);
    this.auditListeners = new ArrayList<AuditListener>(builder.auditListeners);
    this.context = builder.context;
    this.context.setGrammar(parser.getGrammar());
    this.context.getProject().setSourceCodeIndexer(indexer);
    this.commentAnalyser = builder.commentAnalyser;
    this.debugParser = builder.debugParser;
    this.extendedStackTrace = builder.extendedStackTrace;
    indexer.index(context.getProject());
  }

  public SourceCodeSearchEngine getIndex() {
    return indexer;
  }

  public void scanFile(File plSqlFile) {
    scanFiles(Arrays.asList(plSqlFile));
  }

  public void scanFiles(Collection<File> files) {
    for (SquidAstVisitor<? extends Grammar> visitor : visitors) {
      visitor.init();
    }
    for (File file : files) {
      try {
        context.setFile(file);
        AstNode ast = parser.parse(file);
        context.setComments(parser.getLexerOutput().getComments(commentAnalyser));
        AstWalker astWalker = new AstWalker(visitors);
        astWalker.walkAndVisit(ast);
        context.setComments(null);
        context.setFile(null);
        astWalker = null;
      } catch (RecognitionException e) {
        for (SquidAstVisitor<? extends Grammar> visitor: visitors) {
          visitor.visitFile(null);
        }
        
        /* Should we retry with the extended stack trace? */
        boolean extendedStackTracePopulated = false;
        if (this.debugParser != null && this.extendedStackTrace != null) {
          try {
            debugParser.parse(file);
          } catch (RecognitionException re) {
            extendedStackTracePopulated = true;
          } catch (Exception e2) {
          }
        }
        
        if (extendedStackTracePopulated) {
          RecognitionException re = new RecognitionException(extendedStackTrace);
          
          for (AuditListener auditListener: auditListeners) {
            auditListener.processRecognitionException(re);
          }

          LOG.error("Unable to parse source file : " + file.getAbsolutePath() + System.getProperty("line.separator") + re.getMessage());
        } else {
          for (AuditListener auditListener: auditListeners) {
            auditListener.processRecognitionException(e);
          }
          
          LOG.error("Unable to parse source file : " + file.getAbsolutePath() + System.getProperty("line.separator") + e.getMessage());
        }
      	
      	for (SquidAstVisitor<? extends Grammar> visitor: visitors) {
      		visitor.leaveFile(null);
      	}
      } catch (Exception e) {
      	for (SquidAstVisitor<? extends Grammar> visitor: visitors) {
      		visitor.visitFile(null);
      	}
      	
      	for (AuditListener auditListener: auditListeners) {
      		auditListener.processException(e);
      	}
      	
      	for (SquidAstVisitor<? extends Grammar> visitor: visitors) {
      		visitor.leaveFile(null);
      	}
      	
        String errorMessage = "Sonar is unable to analyze file : '" + (file == null ? "null" : file.getAbsolutePath()) + "'";
        throw new AnalysisException(errorMessage, e);
      }
    }
    for (SquidAstVisitor<? extends Grammar> visitor : visitors) {
      visitor.destroy();
    }
  }

  public static <GRAMMAR extends Grammar> Builder<GRAMMAR> builder() {
    return new Builder<GRAMMAR>();
  }

  public static class Builder<GRAMMAR extends Grammar> {

    private Parser<GRAMMAR> parser;
    private List<SquidAstVisitor<GRAMMAR>> visitors = new ArrayList<SquidAstVisitor<GRAMMAR>>();
    private List<AuditListener> auditListeners = new ArrayList<AuditListener>();
    private SquidAstVisitorContextImpl<GRAMMAR> context;
    private CommentAnalyser commentAnalyser;
    private Parser<GRAMMAR> debugParser;
    private ExtendedStackTrace extendedStackTrace;

    public Builder<GRAMMAR> setParser(Parser<GRAMMAR> parser) {
      this.parser = parser;
      return this;
    }

    public Builder<GRAMMAR> setCommentAnalyser(CommentAnalyser commentAnalyser) {
      this.commentAnalyser = commentAnalyser;
      return this;
    }

    public Builder<GRAMMAR> withSquidAstVisitor(SquidAstVisitor<GRAMMAR> visitor) {
      visitor.setContext(context);
      
    	if (visitor instanceof AuditListener) {
    		auditListeners.add((AuditListener)visitor);
    	}
    	
      visitors.add(visitor);
      return this;
    }
    
    public Builder<GRAMMAR> withExtendedStackTrace(Parser<GRAMMAR> debugParser, ExtendedStackTrace extendedStackTrace) {
      this.debugParser = debugParser;
      this.extendedStackTrace = extendedStackTrace;
      return this;
    }

    public Builder<GRAMMAR> setSquidAstVisitorContext(SquidAstVisitorContextImpl<GRAMMAR> context) {
      this.context = context;
      return this;
    }

    public AstScanner<GRAMMAR> build() {
      return new AstScanner<GRAMMAR>(this);
    }
  }
}
