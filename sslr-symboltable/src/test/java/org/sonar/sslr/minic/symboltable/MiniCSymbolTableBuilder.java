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
package org.sonar.sslr.minic.symboltable;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.sonar.sslr.symboltable.*;

public class MiniCSymbolTableBuilder {

  private final SymbolTableBuilder builder = new SymbolTableBuilder();

  public MiniCSymbolTableBuilder(MiniCGrammar grammar) {
    builder.addToFirstPhase(new SymbolTableBuilderVisitor(grammar.compilationUnit) {
      @Override
      public void visitNode(SemanticModel semanticModel, AstNode astNode) {
        Scope currentScope = semanticModel.getEnclosingScope(astNode);

        Scope newCurrentScope = new FileScope(currentScope);
        semanticModel.declareScope(astNode, newCurrentScope);
      }
    });
    builder.addToFirstPhase(new SymbolTableBuilderVisitor(grammar.compoundStatement) {
      @Override
      public void visitNode(SemanticModel semanticModel, AstNode astNode) {
        Scope currentScope = semanticModel.getEnclosingScope(astNode);

        Scope newCurrentScope = new LocalScope(currentScope);
        semanticModel.declareScope(astNode, newCurrentScope);
      }
    });
    builder.addToFirstPhase(new SymbolTableBuilderVisitor(grammar.structDefinition) {
      @Override
      public void visitNode(SemanticModel semanticModel, AstNode astNode) {
        String name = astNode.getChild(1).getTokenValue();
        Scope currentScope = semanticModel.getEnclosingScope(astNode);

        StructSymbol structSymbol = new StructSymbol(currentScope, name);
        semanticModel.declareSymbol(astNode, structSymbol);
        semanticModel.declareScope(astNode, structSymbol);
      }
    });
    builder.addToFirstPhase(new SymbolTableBuilderVisitor(grammar.functionDefinition) {
      @Override
      public void visitNode(SemanticModel semanticModel, AstNode astNode) {
        String name = astNode.getChild(1).getTokenValue();
        Scope currentScope = semanticModel.getEnclosingScope(astNode);

        FunctionSymbol functionSymbol = new FunctionSymbol(currentScope, name);
        semanticModel.declareSymbol(astNode, functionSymbol);
        semanticModel.declareScope(astNode, functionSymbol);
      }
    });
    builder.addToFirstPhase(new SymbolTableBuilderVisitor(grammar.variableDefinition, grammar.parameterDeclaration, grammar.structMember) {
      @Override
      public void visitNode(SemanticModel semanticModel, AstNode astNode) {
        String name = astNode.getChild(1).getTokenValue();
        Scope currentScope = semanticModel.getEnclosingScope(astNode);

        VariableSymbol variableSymbol = new VariableSymbol(currentScope, name, astNode);
        semanticModel.declareSymbol(astNode, variableSymbol);
      }
    });
    builder.addToSecondPhase(new SymbolTableBuilderVisitor(grammar.binVariableReference) {
      @Override
      public void visitNode(SemanticModel semanticModel, AstNode astNode) {
        Scope enclosingScope = semanticModel.getEnclosingScope(astNode);
        String referencedName = astNode.getTokenValue();
        Symbol referencedSymbol = resolve(enclosingScope, VariableSymbol.class, referencedName);
        Preconditions.checkNotNull(referencedSymbol, "Undefined variable.");
        semanticModel.declareReference(astNode, referencedSymbol);
      }
    });
  }

  public static <T extends Symbol> T resolve(Scope scope, Class<T> kind, String key) {
    Symbol symbol = null;
    while (symbol == null && scope != null) {
      symbol = scope.getSymbol(kind, key);
      scope = scope.getEnclosingScope();
    }
    return (T) symbol;
  }

  public SemanticModel buildSymbolTable(AstNode astNode) {
    return builder.buildSymbolTable(astNode);
  }

}
