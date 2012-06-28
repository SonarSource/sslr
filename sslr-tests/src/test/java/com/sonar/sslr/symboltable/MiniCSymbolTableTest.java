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

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.symboltable.*;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCParser;
import com.sonar.sslr.test.symboltable.ScopeTreePrintVisitor;
import org.junit.Test;

import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

public class MiniCSymbolTableTest {

  @Test
  public void test() throws Exception {
    Parser<MiniCGrammar> parser = MiniCParser.create();
    MiniCGrammar grammar = parser.getGrammar();

    // Build AST
    AstNode ast = parser.parse(lines(
        // +1 scope
        "int g;", // +1 symbol
        "struct s {", // +1 symbol and scope
        "  int a;", // +1 symbol
        "}",
        "void myFunction(", // +1 symbol and scope
        "  int p", // +1 symbol
        ") {", // +1 scope
        "  int g = anotherFunction();", // +1 symbol
        "  {", // +1 scope
        "    int l2;", // +1 symbol
        "    g = 0;",
        "    l2 = 1;",
        "  }",
        "  p = 1;",
        "}",
        "int anotherFunction(", // +1 symbol and scope
        ") {", // +1 scope
        "}"));

    // Build Symbol Table
    SymbolTableBuilderContext symbolTable = new MiniCSymbolTableBuilder(grammar).buildSymbolTable(ast);

    ScopeTreePrintVisitor visitor = new ScopeTreePrintVisitor();
    new ScopeTreeWalker(visitor).walk(symbolTable.getEnclosingScope(ast));
    assertThat(visitor.getTotalNumberOfScopes()).isEqualTo(7);
    assertThat(visitor.getTotalNumberOfSymbols()).isEqualTo(8);

    new ScopeTreeWalker(new Detector(symbolTable)).walk(symbolTable.getEnclosingScope(ast));
  }

  private static class Detector implements ScopeTreeVisitor {

    private final SymbolTableBuilderContext symbolTableBuilderContext;

    public Detector(SymbolTableBuilderContext symbolTable) {
      this.symbolTableBuilderContext = symbolTable;
    }

    public void visitScope(Scope scope) {
    }

    public void leaveScope(Scope scope) {
      for (Symbol symbol : scope.getMembers()) {
        if (symbol instanceof VariableSymbol) {
          Collection<AstNode> references = symbolTableBuilderContext.getReferences(symbol);
          if (references.isEmpty()) {
            System.out.println("Unused " + symbol + " at line " + symbol.getAstNode().getTokenLine());
          } else {
            for (AstNode reference : references) {
              System.out.println("line " + reference.getTokenLine() + ": usage of " + symbol);
            }
          }
          if (scope.getEnclosingScope() != null) {
            Symbol hidden = scope.getEnclosingScope().resolve(symbol.getName(), Predicates.instanceOf(VariableSymbol.class));
            if (hidden != null) {
              System.out.println(symbol + " at line " + symbol.getAstNode().getTokenLine() + " hides " + hidden + " from line " + hidden.getAstNode().getTokenLine());
            }
          }
        }
      }
    }
  }

  private static String lines(String... lines) {
    return Joiner.on('\n').join(lines);
  }

}
