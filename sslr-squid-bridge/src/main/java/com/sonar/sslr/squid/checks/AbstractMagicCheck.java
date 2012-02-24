/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;

import java.util.Set;

public abstract class AbstractMagicCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  public abstract Set<AstNodeType> getPatterns();

  public abstract Set<AstNodeType> getInclusions();

  public abstract Set<AstNodeType> getExclusions();

  public abstract String getMessage();

  public abstract boolean isExcepted(AstNode candidate);

  private AstNodeType[] inclusions;
  private AstNodeType[] exclusions;

  private int inclusionLevel;
  private int exclusionLevel;

  @Override
  public void visitFile(AstNode fileNode) {
    inclusionLevel = 0;
    exclusionLevel = 0;
  }

  @Override
  public void init() {
    Set<AstNodeType> patternsSet = getPatterns();
    AstNodeType[] patterns = patternsSet.toArray(new AstNodeType[patternsSet.size()]);

    Set<AstNodeType> inclusionsSet = getInclusions();
    inclusions = inclusionsSet.toArray(new AstNodeType[inclusionsSet.size()]);

    Set<AstNodeType> exclusionsSet = getExclusions();
    exclusions = exclusionsSet.toArray(new AstNodeType[exclusionsSet.size()]);

    subscribeTo(patterns);
    subscribeTo(inclusions);
    subscribeTo(exclusions);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.is(inclusions)) {
      inclusionLevel++;
    } else if (astNode.is(exclusions)) {
      exclusionLevel++;
    } else if ((inclusions.length == 0 || inclusionLevel > 0) && exclusionLevel == 0) {
      if (!isExcepted(astNode)) {
        getContext().createLineViolation(this, getMessage(), astNode);
      }
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    if (astNode.is(inclusions)) {
      inclusionLevel--;
    } else if (astNode.is(exclusions)) {
      exclusionLevel--;
    }
  }

}
