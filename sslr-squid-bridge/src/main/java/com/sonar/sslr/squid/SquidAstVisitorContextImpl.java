/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.io.File;
import java.util.Stack;

import org.sonar.squid.api.CheckMessage;
import org.sonar.squid.api.CodeCheck;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceProject;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Comments;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

public final class SquidAstVisitorContextImpl<GRAMMAR extends Grammar> extends SquidAstVisitorContext<GRAMMAR> implements CodeCheck {

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

  public void setFile(File file) {
    this.file = file;
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
  public void log(String messageText, AstNode node, Object... messageParameters) {
    log(messageText, node.getToken(), messageParameters);
  }

  /** {@inheritDoc} */
  @Override
  public void log(String messageText, Token token, Object... messageParameters) {
    CheckMessage message = new CheckMessage(this, messageText, messageParameters);
    message = new CheckMessage(this, messageText, messageParameters);
    message.setLine(token.getLine());
    log(message);
  }

  /** {@inheritDoc} */
  @Override
  public void log(String messageText, int line, Object... messageParameters) {
    CheckMessage message = new CheckMessage(this, messageText, messageParameters);
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
      throw new IllegalStateException("Unable to log a check message on source code '" + ((peekSourceCode() == null) ? "[NULL]" : peekSourceCode().getKey()) + "'");
    }
  }

  /** {@inheritDoc} */
  public final String getKey() {
    return getClass().getSimpleName();
  }
  
}
