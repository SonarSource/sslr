/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.google.common.base.Joiner;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCParser;
import org.junit.Test;

public class MiniCSymbolTableTest {

  @Test
  public void test() throws Exception {
    Parser<MiniCGrammar> parser = MiniCParser.create();
    MiniCGrammar grammar = parser.getGrammar();

    MiniCSymbolTableBuilder symbolTableBuilder = new MiniCSymbolTableBuilder(grammar);

    // Build AST
    AstNode ast = parser.parse(lines(
        "int g;",
        "struct s {",
        "  int a;",
        "}",
        "void myFunction(int p) {",
        "  int a = anotherFunction();",
        "  { int b; }",
        "  p = 1;",
        "}",
        "int anotherFunction() {",
        "}"));

    // Build tree of scopes and populate it by definitions
    Scope rootScope = symbolTableBuilder.buildScopesTree(ast);

    // At this point we should be able to detect that symbol hides another symbol just by visiting tree of scopes,
    // e.g. local variable hides another local variable, parameter or global variable,
    // or parameter hides global variable
    new ScopeTreeWalker(new ScopeTreePrintVisitor()).walk(rootScope);

    // Resolve type references, resolve identifiers within expressions and on the left side of assignments
    symbolTableBuilder.resolveReferences(ast);

    // At this point we should be able to find where symbols are used,
    // e.g. find unused parameter, unused local variable, assigning value to parameter, "jumbled loop increment"
  }

  private static String lines(String... lines) {
    return Joiner.on('\n').join(lines);
  }

}
