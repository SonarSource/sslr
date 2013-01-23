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
package org.sonar.sslr.internal.ast.query;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCParser;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class CollapsibleIfQueryTest {

  Parser<MiniCGrammar> p = MiniCParser.create();
  MiniCGrammar g = p.getGrammar();

  @Test
  public void test() {
    AstNode fileNode = p.parse(new File("src/test/resources/queries/collapsible_if.mc"));
    List<AstNode> ifStatements = fileNode.getDescendants(g.ifStatement);

    init();

    for (AstNode node : ifStatements) {
      visit(node);
    }

  }

  AstQuery hasIfStatementWithoutElseQuery;
  AstQuery compoundStatementQuery;

  private void init() {
    hasIfStatementWithoutElseQuery = new AstQuery()
        .children(g.statement)
        .children(g.ifStatement)
        .notHaving(new AstQuery().children(g.elseClause));

    compoundStatementQuery = new AstQuery()
        .children(g.statement)
        .children(g.compoundStatement);
  }

  private void visit(AstNode node) {
    if (node.select(new AstQuery().children(g.elseClause)).isEmpty()) { // TODO use compact
      AstResultSet compoundStatement = node.select(compoundStatementQuery);

      if (!node.select(hasIfStatementWithoutElseQuery).isEmpty() ||
        compoundStatement.select(new AstQuery().children()).hasSize(3) && // TODO use compact
        !compoundStatement.select(hasIfStatementWithoutElseQuery).isEmpty()) {

        System.out.println("Found collapsible if statement at line " + node.getTokenLine());
      }
    }
  }

}
