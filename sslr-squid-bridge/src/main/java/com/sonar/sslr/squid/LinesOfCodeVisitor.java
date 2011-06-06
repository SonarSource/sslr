/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import static com.sonar.sslr.api.GenericTokenType.EOF;

import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

/**
 * Visitor that computes the number of lines of code of a file.
 */
public class LinesOfCodeVisitor extends SquidAstVisitor<Grammar> implements AstAndTokenVisitor {

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
    if (token.getType() != EOF && lastTokenLine != token.getLine()) {
      peekSourceCode().add(metric, 1);
      lastTokenLine = token.getLine();
    }
  }

}
