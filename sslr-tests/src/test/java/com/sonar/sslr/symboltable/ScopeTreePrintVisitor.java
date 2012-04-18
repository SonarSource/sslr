/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

public class ScopeTreePrintVisitor implements ScopeTreeVisitor {
  private int depth;

  public void visitScope(Scope scope) {
    System.out.println(pad(depth) + scope + " members=" + scope.getMembers().toString());
    depth++;
  }

  public void leaveScope(Scope scope) {
    depth--;
  }

  private static String pad(int pad) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < pad; i++) {
      sb.append("  ");
    }
    return sb.toString();
  }
}
