/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import org.sonar.api.utils.SonarException;

public abstract class AbstractLineLengthCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> implements AstAndTokenVisitor {

  private int lastIncorrectLine;

  // See SONAR-3164
  public abstract int getMaximumLineLength();

  @Override
  public void init() {
    if (getMaximumLineLength() <= 0) {
      throw new SonarException("[AbstractLineLengthCheck] The maximal line length must be set to a value greater than 0 ("
        + getMaximumLineLength()
        + " given).");
    }
  }

  @Override
  public void visitFile(AstNode astNode) {
    lastIncorrectLine = -1;
  }

  public void visitToken(Token token) {
    if (!token.isGeneratedCode() && lastIncorrectLine != token.getLine() && token.getColumn() + token.getValue().length() > getMaximumLineLength()) {
      lastIncorrectLine = token.getLine();
      getContext().createLineViolation(this, "The line length is greater than {0,number,integer} authorized.", token.getLine(), getMaximumLineLength());
    }
  }

}
