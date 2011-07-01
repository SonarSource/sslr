/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.io.File;

import org.sonar.squid.api.SourceCode;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Comments;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

public abstract class SquidAstVisitorContext<GRAMMAR extends Grammar> {

  public abstract Comments getComments();

  public abstract File getFile();

  public abstract GRAMMAR getGrammar();

  public abstract void addSourceCode(SourceCode child);

  public abstract void popSourceCode();

  public abstract SourceCode peekSourceCode();
  
  /**
   * @param the
   *          message to log
   * @param the
   *          AST node associated to this message. Used to associate the message to a line number.
   * @param the
   *          option message's parameters (see the java.text.MessageFormat class of the java API)
   */
  public abstract void log(String messageText, AstNode node, Object... messageParameters);
  
  /**
   * @param the
   *          message to log
   * @param the
   *          token associated to this message. Used to associate the message to a line number.
   * @param the
   *          option message's parameters (see the java.text.MessageFormat class of the java API)
   */
  public abstract void log(String messageText, Token token, Object... messageParameters);
  
  /**
   * @param the
   *          message to log
   * @param the
   *          line number to associate this to.
   * @param the
   *          option message's parameters (see the java.text.MessageFormat class of the java API)
   */
  public abstract void log(String messageText, int line, Object... messageParameters);
  
}
