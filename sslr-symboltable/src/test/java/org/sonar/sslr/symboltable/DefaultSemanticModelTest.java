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

import org.sonar.sslr.symboltable.DefaultSemanticModel;

import com.sonar.sslr.api.AstNode;
import org.junit.Test;
import org.sonar.sslr.symboltable.Scope;
import org.sonar.sslr.symboltable.SemanticModel;
import org.sonar.sslr.symboltable.Symbol;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultSemanticModelTest {

  @Test
  public void test() {
    SemanticModel semanticModel = new DefaultSemanticModel();

    AstNode scopeAstNode = mock(AstNode.class);
    Scope scope = mock(Scope.class);
    semanticModel.declareScope(scopeAstNode, scope);
    assertThat(semanticModel.getEnclosingScope(scopeAstNode)).isSameAs(scope);
    assertThat(semanticModel.getScopes(Scope.class)).hasSize(1);

    AstNode symbolAstNode = mock(AstNode.class);
    when(symbolAstNode.getParent()).thenReturn(scopeAstNode);
    Symbol symbol = mock(Symbol.class);
    semanticModel.declareSymbol(symbolAstNode, symbol);
    assertThat(semanticModel.getDeclaredSymbol(symbolAstNode)).isSameAs(symbol);
    assertThat(semanticModel.getSymbols(Symbol.class)).hasSize(1);
  }

}
