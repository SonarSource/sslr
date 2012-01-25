/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import org.sonar.api.utils.SonarException;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

public abstract class AbstractNestedIfCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  private int nestingLevel;

  // See SONAR-3164
  public abstract int getMaximumNestingLevel();

  public abstract Rule getIfRule();

  @Override
  public void visitFile(AstNode astNode) {
    nestingLevel = 0;
  }

  @Override
  public void init() {
    if (getMaximumNestingLevel() <= 0) {
      throw new SonarException("[AbstractNestedIfCheck] The maximal if nesting level must be set to a value greater than 0 ("
          + getMaximumNestingLevel()
          + " given).");
    }

    subscribeTo(getIfRule());
  }

  @Override
  public void visitNode(AstNode astNode) {
    nestingLevel++;
    if (nestingLevel > getMaximumNestingLevel()) {
      getContext().createLineViolation(this, "This if has a nesting level of {0}, which is higher than the maximum allowed {1}.", astNode,
          nestingLevel,
          getMaximumNestingLevel());
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    nestingLevel--;
  }

}
