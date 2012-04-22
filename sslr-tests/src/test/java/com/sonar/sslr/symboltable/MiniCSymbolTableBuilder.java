/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.google.common.base.Predicates;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.symboltable.*;
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
        Scope scope = new LocalScope(symbolTable, symbolTable.getEnclosingScope(astNode));
        symbolTable.define(astNode, scope);
      }
    });
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.structDefinition) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        String name = astNode.getChild(1).getTokenValue();
        StructSymbol structSymbol = new StructSymbol(symbolTable, name, symbolTable.getEnclosingScope(astNode));
        symbolTable.define(astNode, structSymbol);
      }
    });
    builder.addToFirstPhase(new SymbolTableElementBuilder(grammar.functionDefinition) {
      @Override
      public void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable) {
        String name = astNode.getChild(1).getTokenValue();
        MethodSymbol methodSymbol = new MethodSymbol(symbolTable, symbolTable.getEnclosingScope(astNode), name);
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
