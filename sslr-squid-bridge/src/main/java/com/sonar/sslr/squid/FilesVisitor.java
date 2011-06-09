/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import org.sonar.squid.api.SourceFile;
import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;

public class FilesVisitor extends SquidAstVisitor<Grammar> {

  private final MetricDef metric;
  private final SquidAstVisitorContext<? extends Grammar> context;

  public FilesVisitor(SquidAstVisitorContext<? extends Grammar> context, MetricDef metric) {
    this.context = context;
    this.metric = metric;
  }

  public void visitFile(AstNode astNode) {
    SourceFile file = new SourceFile(context.getFile().getAbsolutePath().replace('\\', '/'), context.getFile().getName());
    context.addSourceCode(file);
    context.peekSourceCode().setMeasure(metric, 1);
  }

  public void leaveFile(AstNode astNode) {
    context.popSourceCode();
  }

}
