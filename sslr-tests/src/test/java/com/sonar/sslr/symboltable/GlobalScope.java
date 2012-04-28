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
package com.sonar.sslr.symboltable;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.symboltable.Scope;
import com.sonar.sslr.api.symboltable.Symbol;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GlobalScope implements Scope {

  private final List<Symbol> members = Lists.newArrayList();

  public AstNode getAstNode() {
    return null;
  }

  public Scope getEnclosingScope() {
    return null;
  }

  public Collection<Scope> getNestedScopes() {
    return Collections.emptyList();
  }

  public void addNestedScope(Scope nestedScope) {
    throw new UnsupportedOperationException();
  }

  public Collection<Symbol> getMembers() {
    return members;
  }

  public void define(Symbol symbol) {
    members.add(symbol);
  }

  public Symbol lookup(String name, Predicate predicate) {
    return null;
  }

}
