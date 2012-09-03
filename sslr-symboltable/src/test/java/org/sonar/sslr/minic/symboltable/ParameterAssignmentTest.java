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
import com.sonar.sslr.test.miniC.MiniCLexer;
import com.sonar.sslr.test.miniC.MiniCParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.sonar.sslr.symboltable.SemanticModel;
import org.sonar.sslr.symboltable.Symbol;

import java.io.File;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

public class ParameterAssignmentTest {

  @Test
  public void test() throws Exception {
    Parser<MiniCGrammar> parser = MiniCParser.create();
    MiniCGrammar grammar = parser.getGrammar();
    AstNode ast = parser.parse(FileUtils.readFileToString(new File("src/test/resources/parameterAssignment.mc")));
    SemanticModel semanticModel = new MiniCSymbolTableBuilder(grammar).buildSymbolTable(ast);

    int parameterAssignmentsCount = 0;
    Collection<FunctionSymbol> functions = semanticModel.getSymbols(FunctionSymbol.class);
    for (FunctionSymbol function : functions) {
      for (Symbol parameter : function.getMembers()) {
        Collection<AstNode> references = semanticModel.getReferences(parameter);
        for (AstNode reference : references) {
          assertThat(reference.is(grammar.binVariableReference));
          if (reference.nextAstNode().is(MiniCLexer.Punctuators.EQ)) {
            System.out.println("Assignment to parameter '" + parameter.getName() + "' at line " + reference.getTokenLine());
            parameterAssignmentsCount++;
          }
        }
      }
    }
    assertThat(parameterAssignmentsCount).isEqualTo(1);
  }

}
