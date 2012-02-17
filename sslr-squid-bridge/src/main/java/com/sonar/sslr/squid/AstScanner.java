/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ast.AstWalker;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.squid.api.AnalysisException;
import org.sonar.squid.api.SourceCodeSearchEngine;
import org.sonar.squid.api.SourceCodeTreeDecorator;
import org.sonar.squid.api.SourceProject;
import org.sonar.squid.indexer.SquidIndex;
import org.sonar.squid.measures.MetricDef;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.*;

public final class AstScanner<GRAMMAR extends Grammar> {

  private static final Logger LOG = LoggerFactory.getLogger(AstScanner.class);
  private final SquidAstVisitorContextImpl<GRAMMAR> context;
  private final ParserRecoveryListener parserRecoveryListener;
  private final Parser<GRAMMAR> parserProduction;
  private final Parser<GRAMMAR> parserDebug;
  private final List<SquidAstVisitor<GRAMMAR>> visitors;
  private final AuditListener[] auditListeners;
  private final SquidIndex indexer = new SquidIndex();
  private final CommentAnalyser commentAnalyser;
  private final MetricDef[] metrics;
  private final MetricDef filesMetric;

  private AstScanner(Builder<GRAMMAR> builder) {
    this.visitors = new ArrayList<SquidAstVisitor<GRAMMAR>>(builder.visitors);
    this.auditListeners = builder.auditListeners.toArray(new AuditListener[builder.auditListeners.size()]);

    this.parserRecoveryListener = new ParserRecoveryListener();
    this.parserProduction = Parser.builder(builder.baseParser).setRecognictionExceptionListener(parserRecoveryListener).build();

    this.commentAnalyser = builder.commentAnalyser;
    this.context = builder.context;
    this.context.setGrammar(parserProduction.getGrammar());
    this.context.getProject().setSourceCodeIndexer(indexer);
    this.context.setCommentAnalyser(commentAnalyser);
    this.metrics = builder.metrics;
    this.filesMetric = builder.filesMetric;
    indexer.index(context.getProject());

    ParserRecoveryLogger parserRecoveryLogger = new ParserRecoveryLogger();
    parserRecoveryLogger.setContext(this.context);
    this.parserDebug = Parser.builder(builder.baseParser).setParsingEventListeners().setExtendedStackTrace(new ExtendedStackTrace())
        .setRecognictionExceptionListener(this.auditListeners).addRecognictionExceptionListeners(parserRecoveryLogger).build();
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
        context.setFile(file, filesMetric);
        parserRecoveryListener.reset();

        AstNode ast = parserProduction.parse(file);

        // Process the parsing recoveries
        if (parserRecoveryListener.didRecover()) {
          try {
            parserDebug.parse(file);
          } catch (Exception e) {
            LOG.error("Unable to get an extended stack trace on file : " + file.getAbsolutePath(), e);
            LOG.error("Parsing error recoveries not shown.");
          }
        }

        AstWalker astWalker = new AstWalker(visitors);
        astWalker.walkAndVisit(ast);

        context.setFile(null, null);
        astWalker = null;
      } catch (RecognitionException e) {
        LOG.error("Unable to parse source file : " + file.getAbsolutePath());

        try {
          if (e.isToRetryWithExtendStackTrace()) {
            try {
              parserDebug.parse(file);
            } catch (RecognitionException re) {
              e = re;
            } catch (Exception e2) {
              LOG.error("Unable to get an extended stack trace on file : " + file.getAbsolutePath(), e2);
            }

            // Log the recognition exception
            LOG.error(e.getMessage());
          } else {
            LOG.error(e.getMessage(), e);
          }

          // Process the exception
          for (SquidAstVisitor<? extends Grammar> visitor : visitors) {
            visitor.visitFile(null);
          }

          for (AuditListener auditListener : auditListeners) {
            auditListener.processRecognitionException(e);
          }

          for (SquidAstVisitor<? extends Grammar> visitor : visitors) {
            visitor.leaveFile(null);
          }
        } catch (Exception e2) {
          String errorMessage = "Sonar is unable to analyze file : '" + file.getAbsolutePath() + "'";
          throw new AnalysisException(errorMessage, e);
        }
      } catch (Exception e) {
        String errorMessage = "Sonar is unable to analyze file : '" + file.getAbsolutePath() + "'";
        throw new AnalysisException(errorMessage, e);
      }
    }

    for (SquidAstVisitor<? extends Grammar> visitor : visitors) {
      visitor.destroy();
    }

    decorateSquidTree();
  }

  private void decorateSquidTree() {
    if (metrics != null && metrics.length > 0) {
      SourceProject project = context.getProject();
      SourceCodeTreeDecorator decorator = new SourceCodeTreeDecorator(project);
      decorator.decorateWith(metrics);
    }
  }

  public static <GRAMMAR extends Grammar> Builder<GRAMMAR> builder(SquidAstVisitorContextImpl<GRAMMAR> context) {
    return new Builder<GRAMMAR>(context);
  }

  public static class Builder<GRAMMAR extends Grammar> {

    private Parser<GRAMMAR> baseParser;
    private final List<SquidAstVisitor<GRAMMAR>> visitors = new ArrayList<SquidAstVisitor<GRAMMAR>>();
    private final List<AuditListener> auditListeners = new ArrayList<AuditListener>();
    private final SquidAstVisitorContextImpl<GRAMMAR> context;
    private CommentAnalyser commentAnalyser;
    private MetricDef[] metrics;
    private MetricDef filesMetric;

    public Builder(SquidAstVisitorContextImpl<GRAMMAR> context) {
      checkNotNull(context, "context cannot be null");
      this.context = context;
    }

    public Builder<GRAMMAR> setBaseParser(Parser<GRAMMAR> baseParser) {
      checkNotNull(baseParser, "baseParser cannot be null");
      this.baseParser = baseParser;
      return this;
    }

    public Builder<GRAMMAR> setCommentAnalyser(CommentAnalyser commentAnalyser) {
      checkNotNull(commentAnalyser, "commentAnalyser cannot be null");
      this.commentAnalyser = commentAnalyser;
      return this;
    }

    public Builder<GRAMMAR> withSquidAstVisitor(SquidAstVisitor<GRAMMAR> visitor) {
      checkNotNull(visitor, "visitor cannot be null");

      visitor.setContext(context);

      if (visitor instanceof AuditListener) {
        auditListeners.add((AuditListener) visitor);
      }

      visitors.add(visitor);
      return this;
    }

    public Builder<GRAMMAR> withMetrics(MetricDef... metrics) {
      for (MetricDef metric : metrics) {
        checkNotNull(metric, "metrics cannot be null");
      }
      this.metrics = metrics;
      return this;
    }

    public Builder<GRAMMAR> setFilesMetric(MetricDef filesMetric) {
      checkNotNull(filesMetric, "filesMetric cannot be null");
      this.filesMetric = filesMetric;
      return this;
    }

    public AstScanner<GRAMMAR> build() {
      checkState(baseParser != null, "baseParser must be set");
      checkState(commentAnalyser != null, "commentAnalyser must be set");
      checkState(filesMetric != null, "filesMetric must be set");

      return new AstScanner<GRAMMAR>(this);
    }
  }

  private class ParserRecoveryListener implements AuditListener {

    private int recovers = 0;

    public void processRecognitionException(RecognitionException re) {
      if (re.isFatal()) {
        throw new IllegalStateException(
            "ParserRecoveryListener.processRecognitionException() is not supposed to be called with fatal recognition exceptions.", re);
      }

      recovers++;
    }

    public void processException(Exception e) {
      throw new IllegalStateException("ParserRecoveryListener.processException() is not supposed to be called in recovery mode.", e);
    }

    public boolean didRecover() {
      return recovers > 0;
    }

    public void reset() {
      recovers = 0;
    }

  }

  private class ParserRecoveryLogger extends SquidAstVisitor<GRAMMAR> implements AuditListener {

    public void processRecognitionException(RecognitionException re) {
      if (re.isFatal()) {
        throw new IllegalStateException(
            "ParserRecoveryLogger.processRecognitionException() is not supposed to be called with fatal recognition exceptions.", re);
      }

      LOG.warn("Unable to completely parse the file " + getContext().getFile().getAbsolutePath());
      LOG.warn(re.getMessage());
    }

    public void processException(Exception e) {
      throw new IllegalStateException("ParserRecoveryLogger.processException() is not supposed to be called in recovery mode.", e);
    }

  }

}
