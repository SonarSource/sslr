/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;

public abstract class AbstractMagicCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  public abstract AstNodeType[] getPatterns();

  public abstract AstNodeType[] getInclusions();

  public abstract AstNodeType[] getExclusions();

  public abstract String getMessage();

  int inclusionLevel;

  int exclusionLevel;

  @Override
  public void visitFile(AstNode fileNode) {
    inclusionLevel = 0;
    exclusionLevel = 0;
  }

  @Override
  public void init() {
    subscribeTo(getPatterns());
    subscribeTo(getInclusions());
    subscribeTo(getExclusions());
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.is(getInclusions())) {
      inclusionLevel++;
    } else if (astNode.is(getExclusions())) {
      exclusionLevel++;
    } else if ((getInclusions().length == 0 || inclusionLevel > 0) && exclusionLevel == 0) {
      getContext().log(this, getMessage(), astNode);
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    if (astNode.is(getInclusions())) {
      inclusionLevel++;
    } else if (astNode.is(getExclusions())) {
      exclusionLevel--;
    }
  }

}
