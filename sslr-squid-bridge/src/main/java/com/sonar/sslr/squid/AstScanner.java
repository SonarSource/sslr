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
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ast.AstWalker;

public final class AstScanner<GRAMMAR extends Grammar> {

  private static final Logger LOG = LoggerFactory.getLogger(AstScanner.class);
  private SquidAstVisitorContextImpl<GRAMMAR> context;
  private Parser<GRAMMAR> parser;
  private List<SquidAstVisitor<? extends Grammar>> visitors = new ArrayList<SquidAstVisitor<? extends Grammar>>();
  private SquidIndex indexer = new SquidIndex();

  private AstScanner() {
  }

  private AstScanner(Builder<GRAMMAR> builder) {
    this.parser = builder.parser;
    this.visitors = builder.visitors;
    this.context = builder.context;
    this.context.setGrammar(parser.getGrammar());
    this.context.getProject().setSourceCodeIndexer(indexer);
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
        AstNode ast = parser.parse(file);
        context.setComments(parser.getLexerOutput().getComments());
        context.setFile(file);
        AstWalker astWalker = new AstWalker(visitors);
        astWalker.walkAndVisit(ast);
        context.setComments(null);
        context.setFile(null);
        astWalker = null;
      } catch (RecognitionException e) {
        LOG.error("Unable to parse PLSQL source file : " + file.getAbsolutePath(), e);
      } catch (Exception e) {
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
    private List<SquidAstVisitor<? extends Grammar>> visitors = new ArrayList<SquidAstVisitor<? extends Grammar>>();
    private SquidAstVisitorContextImpl<GRAMMAR> context;

    public Builder<GRAMMAR> setParser(Parser<GRAMMAR> parser) {
      this.parser = parser;
      return this;
    }

    public Builder<GRAMMAR> withSquidAstVisitor(SquidAstVisitor<? extends Grammar> visitor) {
      visitors.add(visitor);
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
