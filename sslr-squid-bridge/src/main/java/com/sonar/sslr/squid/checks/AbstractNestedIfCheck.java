/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

public abstract class AbstractNestedIfCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  private final static int DEFAULT_MAXIMUM_NESTING_LEVEL = 3;
  private int nestingLevel;

  @RuleProperty(key = "maximumNestingLevel", description = "The maximum nesting level allowed.", defaultValue = ""
      + DEFAULT_MAXIMUM_NESTING_LEVEL)
  public int maximumNestingLevel = DEFAULT_MAXIMUM_NESTING_LEVEL;

  public abstract Rule getIfRule();

  @Override
  public void visitFile(AstNode astNode) {
    nestingLevel = 0;
  }

  @Override
  public void init() {
    subscribeTo(getIfRule());
  }

  @Override
  public void visitNode(AstNode astNode) {
    nestingLevel++;
    if (nestingLevel > maximumNestingLevel) {
      getContext().log(this, "This if has a nesting level of {0}, which is higher than the maximum allowed {1}.", astNode,
          nestingLevel,
          maximumNestingLevel);
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    nestingLevel--;
  }

}
