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

import com.google.common.base.Predicates;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.symboltable.LocalScope;
import com.sonar.sslr.api.symboltable.Scope;
import com.sonar.sslr.api.symboltable.Symbol;
import com.sonar.sslr.api.symboltable.SymbolTable;
import com.sonar.sslr.api.symboltable.SymbolTableBuilder;
import com.sonar.sslr.api.symboltable.SymbolTableBuilderContext;
import com.sonar.sslr.api.symboltable.SymbolTableElementBuilder;
import com.sonar.sslr.test.miniC.MiniCGrammar;

public class MiniCSymbolTableBuilder {

  private final SymbolTableBuilder builder = new SymbolTableBuilder();

  public MiniCSymbolTableBuilder(MiniCGrammar grammar) {
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.compilationUnit) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        Scope scope = new LocalScope(symbolTable);
        symbolTable.define(astNode, scope);
      }
    });
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.compoundStatement) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        Scope scope = new LocalScope(symbolTable);
        symbolTable.define(astNode, scope);
      }
    });
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.structDefinition) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        String name = astNode.getChild(1).getTokenValue();
        StructSymbol structSymbol = new StructSymbol(symbolTable, name);
        symbolTable.define(astNode, structSymbol);
      }
    });
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.functionDefinition) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        String name = astNode.getChild(1).getTokenValue();
        MethodSymbol methodSymbol = new MethodSymbol(symbolTable, name);
        symbolTable.define(astNode, methodSymbol);
      }
    });
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.variableDefinition, grammar.parameterDeclaration, grammar.structMember) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        String name = astNode.getChild(1).getTokenValue();
        VariableSymbol variableSymbol = new VariableSymbol(symbolTable, name);
        symbolTable.define(astNode, variableSymbol);
      }
    });

    builder.addToSecondPhase(new SymbolTableElementBuilder(grammar.binVariableReference) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        Scope enclosingScope = symbolTable.getEnclosingScope(astNode);
        String referencedName = astNode.getTokenValue();
        Symbol referencedSymbol = enclosingScope.lookup(referencedName, Predicates.instanceOf(VariableSymbol.class));
        symbolTable.addReference(astNode, referencedSymbol);
      }
    });
  }

  public SymbolTable buildSymbolTable(AstNode astNode) {
    return builder.buildSymbolTable(astNode);
  }

}
