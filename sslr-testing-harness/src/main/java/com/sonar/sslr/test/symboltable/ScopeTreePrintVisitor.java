/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.symboltable;

import com.sonar.sslr.api.symboltable.Scope;
import com.sonar.sslr.api.symboltable.ScopeTreeVisitor;

public class ScopeTreePrintVisitor implements ScopeTreeVisitor {

  private int depth;

  private int totalNumberOfScopes;
  private int totalNumberOfSymbols;

  public void visitScope(Scope scope) {
    totalNumberOfScopes++;
    totalNumberOfSymbols += scope.getMembers().size();

    System.out.print(pad(depth) + scope);
    if (!scope.getMembers().isEmpty()) {
      System.out.print(" members=" + scope.getMembers().toString());
    }
    System.out.println();
    depth++;
  }

  public void leaveScope(Scope scope) {
    depth--;
  }

  public int getTotalNumberOfScopes() {
    return totalNumberOfScopes;
  }

  public int getTotalNumberOfSymbols() {
    return totalNumberOfSymbols;
  }

  private static String pad(int pad) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < pad; i++) {
      sb.append("  ");
    }
    return sb.toString();
  }

}
