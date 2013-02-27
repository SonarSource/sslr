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
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniCParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.sonar.sslr.symboltable.SemanticModel;

import java.io.File;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

/**
 * This test demonstrates how to find unused function parameters.
 */
public class UnusedParameterTest {

  @Test
  public void test() throws Exception {
    Parser<Grammar> parser = MiniCParser.create();
    AstNode ast = parser.parse(FileUtils.readFileToString(new File("src/test/resources/unusedParameter.mc")));
    SemanticModel semanticModel = new MiniCSymbolTableBuilder().buildSymbolTable(ast);

    int unusedParametersCount = 0;
    Collection<FunctionSymbol> functions = semanticModel.getSymbols(FunctionSymbol.class);
    for (FunctionSymbol function : functions) {
      for (VariableSymbol parameter : function.getSymbols(VariableSymbol.class)) {
        if (semanticModel.getReferences(parameter).isEmpty()) {
          System.out.println("Unused parameter '" + parameter.getKey() + "' at line " + parameter.getLine() + " in function '" + function.getKey() + "'");
          unusedParametersCount++;
        }
      }
    }
    assertThat(unusedParametersCount).isEqualTo(1);
  }

}
