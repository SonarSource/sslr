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
package com.sonar.sslr.api.symboltable;

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
