/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import java.io.File;

import org.sonar.squid.api.CodeCheck;
import org.sonar.squid.api.SourceCode;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.CommentAnalyser;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

public abstract class SquidAstVisitorContext<GRAMMAR extends Grammar> {

  public abstract File getFile();

  public abstract GRAMMAR getGrammar();

  public abstract void addSourceCode(SourceCode child);

  public abstract void popSourceCode();

  public abstract SourceCode peekSourceCode();

  public abstract CommentAnalyser getCommentAnalyser();

  /**
   * Create a new file violation
   * 
   * @param check
   *          the check which is creating this new violation (i.e. this function's caller)
   * @param message
   *          message describing the violation, can be formatted (see java.text.MessageFormat)
   * @param messageParameters
   *          optional message parameters (see java.text.MessageFormat)
   */
  public abstract void createFileViolation(CodeCheck check, String message, Object... messageParameters);

  /**
   * Create a new line violation caused by a given AST node
   * 
   * @param check
   *          the check which is creating this new violation (i.e. this function's caller)
   * @param message
   *          message describing the violation, can be formatted (see java.text.MessageFormat)
   * @param node
   *          AST node which causing the violation
   * @param messageParameters
   *          optional message parameters (see java.text.MessageFormat)
   */
  public abstract void createLineViolation(CodeCheck check, String message, AstNode node, Object... messageParameters);

  /**
   * Create a new line violation caused by a given token
   * 
   * @param check
   *          the check which is creating this new violation (i.e. this function's caller)
   * @param message
   *          message describing the violation, can be formatted (see java.text.MessageFormat)
   * @param token
   *          Token which causing the violation
   * @param messageParameters
   *          optional message parameters (see java.text.MessageFormat)
   */
  public abstract void createLineViolation(CodeCheck check, String message, Token token, Object... messageParameters);

  /**
   * Create a new line violation, not directly caused by an AST node nor a Token
   * 
   * @param check
   *          the check which is creating this new violation (i.e. this function's caller)
   * @param message
   *          message describing the violation, can be formatted (see java.text.MessageFormat)
   * @param line
   *          line on which the violation must be created.
   *          If zero or a negative number is passed, a file violation will be created instead of a line one
   * @param messageParameters
   *          optional message parameters (see java.text.MessageFormat)
   */
  public abstract void createLineViolation(CodeCheck check, String message, int line, Object... messageParameters);

}
