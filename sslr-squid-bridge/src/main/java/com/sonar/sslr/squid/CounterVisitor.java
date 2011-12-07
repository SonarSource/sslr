/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;

public class CounterVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> {

  private final MetricDef metric;
  private List<AstNodeType> astNodeTypes = new LinkedList<AstNodeType>();

  public static <GRAMMAR extends Grammar> Builder<GRAMMAR> builder() {
    return new Builder<GRAMMAR>();
  }

  public static class Builder<GRAMMAR extends Grammar> {

    private MetricDef metric;
    private List<AstNodeType> astNodeTypes = new LinkedList<AstNodeType>();

    private Builder() {
    }

    public Builder<GRAMMAR> setMetricDef(MetricDef metric) {
      this.metric = metric;
      return this;
    }

    public Builder<GRAMMAR> subscribeTo(AstNodeType astNodeType) {
      this.astNodeTypes.add(astNodeType);
      return this;
    }

    public Builder<GRAMMAR> subscribeTo(AstNodeType... astNodeTypes) {
      for (AstNodeType astNodeType : astNodeTypes) {
        this.astNodeTypes.add(astNodeType);
      }

      return this;
    }

    public Builder<GRAMMAR> subscribeTo(Collection<AstNodeType> astNodeTypes) {
      this.astNodeTypes = new LinkedList<AstNodeType>(astNodeTypes);
      return this;
    }

    public CounterVisitor<GRAMMAR> build() {
      return new CounterVisitor<GRAMMAR>(this);
    }

  }

  private CounterVisitor(Builder<GRAMMAR> builder) {
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
    getContext().peekSourceCode().add(metric, 1);
  }

}
