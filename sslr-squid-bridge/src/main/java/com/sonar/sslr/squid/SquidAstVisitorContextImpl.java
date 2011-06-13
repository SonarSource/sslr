/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.io.File;
import java.util.Stack;

import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceProject;

import com.sonar.sslr.api.Comments;
import com.sonar.sslr.api.Grammar;

public class SquidAstVisitorContextImpl<GRAMMAR extends Grammar> extends SquidAstVisitorContext<GRAMMAR> {

  private Stack<SourceCode> sourceCodeStack = new Stack<SourceCode>();
  private GRAMMAR grammar;
  private Comments comments;
  private File file;
  private SourceProject project;

  public SquidAstVisitorContextImpl(SourceProject project) {
    this.project = project;
    sourceCodeStack.add(project);
  }

  public void setGrammar(GRAMMAR grammar) {
    this.grammar = grammar;
  }

  public void setComments(Comments comments) {
    this.comments = comments;
  }

  @Override
  public final Comments getComments() {
    return comments;
  }

  @Override
  public void addSourceCode(SourceCode child) {
    peekSourceCode().addChild(child);
    sourceCodeStack.add(child);
  }

  @Override
  public void popSourceCode() {
    sourceCodeStack.pop();
  }

  @Override
  public final SourceCode peekSourceCode() {
    return sourceCodeStack.peek();
  }

  public void setFile(File file) {
    this.file = file;
  }

  @Override
  public final File getFile() {
    return file;
  }

  public final SourceProject getProject() {
    return project;
  }

  @Override
  public GRAMMAR getGrammar() {
    return grammar;
  }
}
