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
package org.sonar.sslr.symboltable;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractScopeTest {

  @Test
  public void scopes() {
    Scope enclosingScope = mock(Scope.class);
    AbstractScope scope = new FakeScope(enclosingScope);
    assertThat(scope.getEnclosingScope()).isSameAs(enclosingScope);
    assertThat(scope.getNestedScopes()).hasSize(0);
    Scope foo = mock(Scope.class);
    Scope bar = mock(Scope.class);
    scope.addNestedScope(foo);
    scope.addNestedScope(bar);
    assertThat(scope.getNestedScopes()).containsExactly(foo, bar);
  }

  @Test
  public void symbols() {
    AbstractScope scope = new FakeScope(null);
    assertThat(scope.getSymbols()).hasSize(0);
    Symbol foo = mock(Symbol.class);
    when(foo.getKey()).thenReturn("foo");
    FakeSymbol bar = mock(FakeSymbol.class);
    when(bar.getKey()).thenReturn("bar");
    scope.addSymbol(foo);
    scope.addSymbol(bar);
    assertThat(scope.getSymbols()).containsExactly(foo, bar);
    assertThat(scope.getSymbols(Symbol.class)).containsExactly(foo, bar);
    assertThat(scope.getSymbols(FakeSymbol.class)).containsExactly(bar);
    assertThat(scope.getSymbol(Symbol.class, "foo")).isSameAs(foo);
    assertThat(scope.getSymbol(FakeSymbol.class, "foo")).isNull();
    assertThat(scope.getSymbol(Symbol.class, "bar")).isSameAs(bar);
    assertThat(scope.getSymbol(FakeSymbol.class, "bar")).isSameAs(bar);
  }

  private static interface FakeSymbol extends Symbol {
  }

  private static class FakeScope extends AbstractScope {
    public FakeScope(Scope enclosingScope) {
      super(enclosingScope);
    }
  }

}
