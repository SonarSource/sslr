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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.AstVisitor;
import com.sonar.sslr.impl.ast.AstWalker;

import java.util.List;

/**
 * Implementation of two-phase algorithm for building {@link SymbolTable} based on AST.
 *
 * <p>
 * Commonly, the first phase is used to create scopes and define the symbols,
 * and the second phase is used to track the usage of those already defined symbols.
 *
 * In languages where forward references are not allowed, those two phases can be easily
 * merged in a single one.
 * </p>
 */
public class SymbolTableBuilder {

  private final List<SymbolTableElementBuilder> firstPhase = Lists.newArrayList();
  private final List<SymbolTableElementBuilder> secondPhase = Lists.newArrayList();

  public SymbolTableBuilder addToFirstPhase(SymbolTableElementBuilder elementBuilder) {
    firstPhase.add(elementBuilder);
    return this;
  }

  public SymbolTableBuilder addToSecondPhase(SymbolTableElementBuilder elementBuilder) {
    secondPhase.add(elementBuilder);
    return this;
  }

  /**
   * Builds Symbol Table starting from specified AST node.
   */
  public SymbolTable buildSymbolTable(AstNode astNode) {
    // Build tree of scopes and populate it by definitions

    SymbolTableBuilderContext symbolTable = new SymbolTableBuilderContext();

    // At this point we should be able to detect that symbol hides another symbol just by visiting tree of scopes,
    // e.g. local variable hides another local variable, parameter or global variable,
    // or parameter hides global variable

    // Resolve type references, resolve identifiers within expressions and on the left side of assignments

    new AstWalker(createVisitors(symbolTable, firstPhase)).walkAndVisit(astNode);

    // At this point we should be able to find where symbols are used,
    // e.g. find unused parameter, unused local variable, assigning value to parameter, "jumbled loop increment"

    new AstWalker(createVisitors(symbolTable, secondPhase)).walkAndVisit(astNode);

    return symbolTable;
  }

  private static List<AstVisitor> createVisitors(SymbolTableBuilderContext symbolTable, List<SymbolTableElementBuilder> builders) {
    ImmutableList.Builder<AstVisitor> visitors = ImmutableList.builder();
    for (SymbolTableElementBuilder builder : builders) {
      visitors.add(new AstVisitorForSymbolTable(builder, symbolTable));
    }
    return visitors.build();
  }

  /**
   * {@link AstVisitor} for construction of symbol table.
   */
  private static class AstVisitorForSymbolTable implements AstVisitor {

    private final SymbolTableElementBuilder builder;
    private final SymbolTableBuilderContext symbolTableBuilderContext;

    public AstVisitorForSymbolTable(SymbolTableElementBuilder builder, SymbolTableBuilderContext symbolTableBuilderContext) {
      this.builder = builder;
      this.symbolTableBuilderContext = symbolTableBuilderContext;
    }

    public List<AstNodeType> getAstNodeTypesToVisit() {
      return builder.getNodeTypes();
    }

    public void visitFile(AstNode astNode) {
      // nop
    }

    public void leaveFile(AstNode astNode) {
      // nop
    }

    public void visitNode(AstNode astNode) {
      builder.visitNode(astNode, symbolTableBuilderContext);
    }

    public void leaveNode(AstNode astNode) {
      // nop
    }

  }

}
