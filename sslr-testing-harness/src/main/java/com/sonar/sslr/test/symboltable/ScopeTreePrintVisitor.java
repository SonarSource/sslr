/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
