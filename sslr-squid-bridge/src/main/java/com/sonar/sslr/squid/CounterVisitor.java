/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.util.Collection;
import java.util.LinkedList;

import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;

public class CounterVisitor extends SquidAstVisitor<Grammar> {

  private MetricDef metric;
  private LinkedList<AstNodeType> astNodeTypes = new LinkedList<AstNodeType>();
  private final SquidAstVisitorContext<? extends Grammar> context;

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private MetricDef metric;
    private LinkedList<AstNodeType> astNodeTypes = new LinkedList<AstNodeType>();
    private SquidAstVisitorContext<? extends Grammar> context;

    private Builder() {
    }

    public Builder setMetricDef(MetricDef metric) {
      this.metric = metric;
      return this;
    }

    public Builder setContext(SquidAstVisitorContext<? extends Grammar> context) {
      this.context = context;
      return this;
    }

    public Builder subscribeTo(AstNodeType astNodeType) {
      this.astNodeTypes.add(astNodeType);
      return this;
    }

    public Builder subscribeTo(AstNodeType... astNodeTypes) {
      this.astNodeTypes = new LinkedList<AstNodeType>();

      for (AstNodeType astNodeType : astNodeTypes) {
        this.astNodeTypes.add(astNodeType);
      }

      return this;
    }

    public Builder subscribeTo(Collection<AstNodeType> astNodeTypes) {
      this.astNodeTypes = new LinkedList<AstNodeType>(astNodeTypes);
      return this;
    }

    public CounterVisitor build() {
      return new CounterVisitor(context, this);
    }

  }

  private CounterVisitor(SquidAstVisitorContext<? extends Grammar> context, Builder builder) {
    this.context = context;
    this.metric = builder.metric;
    this.astNodeTypes = builder.astNodeTypes;
  }

  @Override
  public void init() {
    for (AstNodeType astNodeType : astNodeTypes) {
      subscribeTo(astNodeType);
    }
  }

  @Override
  public void visitNode(AstNode astNode) {
    context.peekSourceCode().add(metric, 1);
  }

}
