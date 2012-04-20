/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.symboltable.LocalScope;

import com.sonar.sslr.api.symboltable.SymbolTableBuilder;
import com.sonar.sslr.api.symboltable.SymbolTableBuilderContext;
import com.sonar.sslr.api.symboltable.SymbolTableElementBuilder;

import com.google.common.base.Predicates;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.symboltable.Scope;
import com.sonar.sslr.api.symboltable.Symbol;
import com.sonar.sslr.api.symboltable.SymbolTable;
import com.sonar.sslr.test.miniC.MiniCGrammar;

public class MiniCSymbolTableBuilder {

  private static final GlobalScope globalScope;

  static {
    // Build global scope with built-in-types, it should be reusable - no need to build it for each compilation unit
    globalScope = new GlobalScope();
    globalScope.define(new BuiltInType("int"));
    globalScope.define(new BuiltInType("void"));
  }

  private final SymbolTableBuilder builder = new SymbolTableBuilder();

  public MiniCSymbolTableBuilder(MiniCGrammar grammar) {
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.compoundStatement) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        Scope scope = new LocalScope(astNode, symbolTable.getEnclosingScope(astNode));
        symbolTable.defineScope(astNode, scope);
      }
    });
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.structDefinition) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        String name = astNode.getChild(1).getTokenValue();
        StructSymbol structSymbol = new StructSymbol(astNode, name, symbolTable.getEnclosingScope(astNode));
        symbolTable.defineSymbol(astNode, structSymbol);
        symbolTable.defineScope(astNode, structSymbol);
      }
    });
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.functionDefinition) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        String name = astNode.getChild(1).getTokenValue();
        MethodSymbol methodSymbol = new MethodSymbol(astNode, symbolTable.getEnclosingScope(astNode), name);
        symbolTable.defineSymbol(astNode, methodSymbol);
        symbolTable.defineScope(astNode, methodSymbol);
      }
    });
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.variableDefinition, grammar.parameterDeclaration, grammar.structMember) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        String name = astNode.getChild(1).getTokenValue();
        VariableSymbol variableSymbol = new VariableSymbol(astNode, name);
        symbolTable.defineSymbol(astNode, variableSymbol);
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
    Scope rootScope = new LocalScope(astNode);
    rootScope.importScope(globalScope);
    return builder.buildSymbolTable(astNode, rootScope);
  }

}
