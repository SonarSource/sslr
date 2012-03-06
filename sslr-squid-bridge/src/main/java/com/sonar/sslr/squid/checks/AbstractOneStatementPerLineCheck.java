/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.google.common.collect.Maps;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

import java.util.Map;

public abstract class AbstractOneStatementPerLineCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  private final Map<Integer, Integer> statementsPerLine = Maps.newHashMap();

  public abstract Rule getStatementRule();

  public abstract boolean isExcluded(AstNode statementNode);

  @Override
  public void init() {
    subscribeTo(getStatementRule());
  }

  @Override
  public void visitFile(AstNode astNode) {
    statementsPerLine.clear();
  }

  @Override
  public void visitNode(AstNode statementNode) {
    if (!isExcluded(statementNode)) {
      int line = statementNode.getTokenLine();

      if (!statementsPerLine.containsKey(line)) {
        statementsPerLine.put(line, 0);
      }

      statementsPerLine.put(line, statementsPerLine.get(line) + 1);
    }
  }

  @Override
  public void leaveFile(AstNode astNode) {
    for (Map.Entry<Integer, Integer> statementsAtLine : statementsPerLine.entrySet()) {
      if (statementsAtLine.getValue() > 1) {
        getContext().createLineViolation(this, "At most one statement is allowed per line, but {0} statements were found on this line.", statementsAtLine.getKey(),
            statementsAtLine.getValue());
      }
    }
  }

}
