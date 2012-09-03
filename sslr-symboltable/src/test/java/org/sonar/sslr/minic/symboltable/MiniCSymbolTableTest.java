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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.symboltable.Scope;
import org.sonar.sslr.symboltable.SemanticModel;
import org.sonar.sslr.symboltable.Symbol;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class MiniCSymbolTableTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Parser<MiniCGrammar> parser = MiniCParser.create();
  private MiniCGrammar grammar = parser.getGrammar();

  @Test
  public void test() throws Exception {
    AstNode ast = parser.parse(FileUtils.readFileToString(new File("src/test/resources/symbol.mc")));
    SemanticModel semanticModel = new MiniCSymbolTableBuilder(grammar).buildSymbolTable(ast);

    assertThat(semanticModel.getScopes(Scope.class).size()).isEqualTo(7);
    assertThat(semanticModel.getSymbols(Symbol.class).size()).isEqualTo(8);
  }

  @Test
  public void undeclared_variable() throws Exception {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("Undefined variable.");

    AstNode ast = parser.parse(FileUtils.readFileToString(new File("src/test/resources/undeclaredVariable.mc")));
    new MiniCSymbolTableBuilder(grammar).buildSymbolTable(ast);
  }

}
