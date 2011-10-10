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

public class FilesVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> {

  private final MetricDef metric;

  public FilesVisitor(MetricDef metric) {
    this.metric = metric;
  }

  public void visitFile(AstNode astNode) {
    SourceFile file = new SourceFile(getContext().getFile().getAbsolutePath(), getContext().getFile().getName());
    getContext().addSourceCode(file);
    getContext().peekSourceCode().setMeasure(metric, 1);
  }

  public void leaveFile(AstNode astNode) {
    getContext().popSourceCode();
  }

}
