/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

public abstract class AbstractGotoCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  public abstract Rule getGotoRule();

  @Override
  public void init() {
    subscribeTo(getGotoRule());
  }

  @Override
  public void visitNode(AstNode astNode) {
    getContext().createLineViolation(this, "Goto should be avoided.", astNode);
  }

}
