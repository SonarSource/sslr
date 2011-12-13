/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.metrics;

import static com.sonar.sslr.api.GenericTokenType.*;

import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.squid.SquidAstVisitor;

/**
 * Visitor that computes the number of lines of code of a file.
 */
public class LinesOfCodeVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> implements AstAndTokenVisitor {

  private final MetricDef metric;
  private int lastTokenLine;

  public LinesOfCodeVisitor(MetricDef metric) {
    this.metric = metric;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visitFile(AstNode node) {
    lastTokenLine = -1;
  }

  /**
   * {@inheritDoc}
   */
  public void visitToken(Token token) {
    if (token.getType() != EOF) {
      /* Handle all the lines of the token */
      String[] tokenLines = token.getValue().split("\n", -1);

      int firstLineAlreadyCounted = lastTokenLine == token.getLine() ? 1 : 0;
      getContext().peekSourceCode().add(metric, tokenLines.length - firstLineAlreadyCounted);

      lastTokenLine = token.getLine() + tokenLines.length - 1;
    }
  }

}
