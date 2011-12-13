/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.metrics;

import static com.sonar.sslr.api.GenericTokenType.*;

import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.squid.SquidAstVisitor;

/**
 * Visitor that computes the number of lines of a file.
 */
public class LinesVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> implements AstAndTokenVisitor {

  private final MetricDef metric;

  public LinesVisitor(MetricDef metric) {
    this.metric = metric;
  }

  /**
   * {@inheritDoc}
   */
  public void visitToken(Token token) {
    if (token.getType() == EOF) {
      getContext().peekSourceCode().setMeasure(metric, token.getLine());
    }
  }

}
