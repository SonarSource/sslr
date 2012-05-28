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

import com.sonar.sslr.api.AstNode;

/**
 * Default implementation of {@link Symbol}.
 */
public abstract class BaseSymbol implements Symbol {

  private final Scope scope;
  private final String name;
  private final AstNode astNode;

  public BaseSymbol(Scope scope, String name, AstNode astNode) {
    this.scope = scope;
    this.name = name;
    this.astNode = astNode;
  }

  public AstNode getAstNode() {
    return astNode;
  }

  public Scope getEnclosingScope() {
    return scope;
  }

  public String getName() {
    return name;
  }

}
