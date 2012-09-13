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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.sonar.sslr.symboltable.Scope;
import org.sonar.sslr.symboltable.SemanticModel;

import java.io.File;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

/**
 * This test demonstrates how to find variables that hide other variables.
 */
public class HiddenVariableTest {

  @Test
  public void test() throws Exception {
    Parser<MiniCGrammar> parser = MiniCParser.create();
    MiniCGrammar grammar = parser.getGrammar();
    AstNode ast = parser.parse(FileUtils.readFileToString(new File("src/test/resources/hiddenVariable.mc")));
    SemanticModel semanticModel = new MiniCSymbolTableBuilder(grammar).buildSymbolTable(ast);

    int hiddenVariablesCount = 0;
    Collection<VariableSymbol> variables = semanticModel.getSymbols(VariableSymbol.class);
    for (VariableSymbol variable : variables) {
      Scope scope = variable.getEnclosingScope();
      VariableSymbol hiddenVariable = MiniCSymbolTableBuilder.resolve(scope.getEnclosingScope(), VariableSymbol.class, variable.getKey());
      if (hiddenVariable != null) {
        System.out.println("Variable '" + variable.getKey() + "' at line " + variable.getLine() + " hides variable at line " + hiddenVariable.getLine());
        hiddenVariablesCount++;
      }
    }
    assertThat(hiddenVariablesCount).isEqualTo(2);
  }

}
