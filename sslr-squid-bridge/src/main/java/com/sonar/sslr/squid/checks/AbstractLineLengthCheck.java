/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

public abstract class AbstractLineLengthCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> implements AstAndTokenVisitor {

  private final static int DEFAULT_MAXIMUM_LINE_LENHGTH = 80;
  private int lastIncorrectLine;

  @RuleProperty(key = "maximumLineLength", description = "The maximum authorized line length.", defaultValue = ""
      + DEFAULT_MAXIMUM_LINE_LENHGTH)
  public int maximumLineLength = DEFAULT_MAXIMUM_LINE_LENHGTH;

  @Override
  public void visitFile(AstNode astNode) {
    lastIncorrectLine = -1;
  }

  public void visitToken(Token token) {
    if (lastIncorrectLine != token.getLine() && token.getColumn() + token.getValue().length() > maximumLineLength) {
      lastIncorrectLine = token.getLine();
      getContext().log(this, "The line length is greater than {0,number,integer} authorized.", token.getLine(), maximumLineLength);
    }
  }

}
