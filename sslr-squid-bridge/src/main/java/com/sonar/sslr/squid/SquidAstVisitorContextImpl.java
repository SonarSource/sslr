/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.io.File;
import java.util.Stack;

import org.sonar.squid.api.*;
import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Comments;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

public final class SquidAstVisitorContextImpl<GRAMMAR extends Grammar> extends SquidAstVisitorContext<GRAMMAR> {

  private final Stack<SourceCode> sourceCodeStack = new Stack<SourceCode>();
  private GRAMMAR grammar;
  private Comments comments;
  private File file;
  private final SourceProject project;

  public SquidAstVisitorContextImpl(SourceProject project) {
    if (project == null) {
      throw new IllegalArgumentException("project cannot be null.");
    }

    this.project = project;
    sourceCodeStack.add(project);
  }

  public void setGrammar(GRAMMAR grammar) {
    this.grammar = grammar;
  }

  public void setComments(Comments comments) {
    this.comments = comments;
  }

  /** {@inheritDoc} */
  @Override
  public final Comments getComments() {
    return comments;
  }

  /** {@inheritDoc} */
  @Override
  public void addSourceCode(SourceCode child) {
    peekSourceCode().addChild(child);
    sourceCodeStack.add(child);
  }

  /** {@inheritDoc} */
  @Override
  public void popSourceCode() {
    sourceCodeStack.pop();
  }

  /** {@inheritDoc} */
  @Override
  public final SourceCode peekSourceCode() {
    return sourceCodeStack.peek();
  }

  public void setFile(File file, MetricDef filesMetric) {
    peekTillSourceProject();
    this.file = file;
    if (file != null) {
      SourceFile sourceFile = new SourceFile(file.getAbsolutePath(), file.getName());
      addSourceCode(sourceFile);
      peekSourceCode().setMeasure(filesMetric, 1);
    }
  }

  private void peekTillSourceProject() {
    while ( !(peekSourceCode() instanceof SourceProject)) {
      popSourceCode();
    }
  }

  /** {@inheritDoc} */
  @Override
  public final File getFile() {
    return file;
  }

  public final SourceProject getProject() {
    return project;
  }

  /** {@inheritDoc} */
  @Override
  public GRAMMAR getGrammar() {
    return grammar;
  }

  /** {@inheritDoc} */
  @Override
  public void log(CodeCheck codeCheck, String messageText, AstNode node, Object... messageParameters) {
    log(codeCheck, messageText, node.getToken(), messageParameters);
  }

  /** {@inheritDoc} */
  @Override
  public void log(CodeCheck codeCheck, String messageText, Token token, Object... messageParameters) {
    CheckMessage message = new CheckMessage(codeCheck, messageText, messageParameters);
    message.setLine(token.getLine());
    log(message);
  }

  /** {@inheritDoc} */
  @Override
  public void log(CodeCheck codeCheck, String messageText, int line, Object... messageParameters) {
    CheckMessage message = new CheckMessage(codeCheck, messageText, messageParameters);
    if (line > 0) {
      message.setLine(line);
    }
    log(message);
  }

  private void log(CheckMessage message) {
    if (peekSourceCode() instanceof SourceFile) {
      peekSourceCode().log(message);
    } else if (peekSourceCode().getParent(SourceFile.class) != null) {
      peekSourceCode().getParent(SourceFile.class).log(message);
    } else {
      throw new IllegalStateException("Unable to log a check message on source code '"
          + (peekSourceCode() == null ? "[NULL]" : peekSourceCode().getKey()) + "'");
    }
  }

  /** {@inheritDoc} */
  public final String getKey() {
    return getClass().getSimpleName();
  }

}
