/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import java.util.Arrays;
import java.util.List;

public class ScopeTreeWalker {

  private final List<ScopeTreeVisitor> visitors;

  public ScopeTreeWalker(ScopeTreeVisitor... visitors) {
    this.visitors = Arrays.asList(visitors);
  }

  public void walk(Scope scope) {
    visit(scope);
    for (Scope nestedScope : scope.getNestedScopes()) {
      walk(nestedScope);
    }
    leave(scope);
  }

  private void visit(Scope scope) {
    for (ScopeTreeVisitor visitor : visitors) {
      visitor.visitScope(scope);
    }
  }

  private void leave(Scope scope) {
    for (ScopeTreeVisitor visitor : visitors) {
      visitor.leaveScope(scope);
    }
  }

}
