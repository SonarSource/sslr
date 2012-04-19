/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.symboltable.*;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCParser;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
    SymbolTable symbolTable = new MiniCSymbolTableBuilder(grammar).buildSymbolTable(ast);

    new ScopeTreeWalker(new ScopeTreePrintVisitor()).walk(symbolTable.getEnclosingScope(ast));

    Counter counter = new Counter(symbolTable);
    new ScopeTreeWalker(counter).walk(symbolTable.getEnclosingScope(ast));
    assertThat(counter.symbols, is(8));
    assertThat(counter.scopes, is(7));
  }

  private static class Counter implements ScopeTreeVisitor {

    private final SymbolTable symbolTable;

    private int symbols;
    private int scopes;

    public Counter(SymbolTable symbolTable) {
      this.symbolTable = symbolTable;
    }

    public void visitScope(Scope scope) {
      symbols += scope.getMembers().size();
      scopes++;
    }

    public void leaveScope(Scope scope) {
      for (Symbol symbol : scope.getMembers()) {
        if (symbol instanceof VariableSymbol) {
          Collection<AstNode> references = symbolTable.getReferences(symbol);
          if (references.isEmpty()) {
            System.out.println("Unused " + symbol + " at line " + symbol.getAstNode().getTokenLine());
          } else {
            for (AstNode reference : references) {
              System.out.println("line " + reference.getTokenLine() + ": usage of " + symbol);
            }
          }
          if (scope.getEnclosingScope() != null) {
            Symbol hidden = scope.getEnclosingScope().lookup(symbol.getName(), Predicates.instanceOf(VariableSymbol.class));
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
