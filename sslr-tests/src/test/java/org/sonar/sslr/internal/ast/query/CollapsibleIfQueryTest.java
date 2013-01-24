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

import static org.fest.assertions.Assertions.assertThat;

public class CollapsibleIfQueryTest {

  Parser<MiniCGrammar> p = MiniCParser.create();
  MiniCGrammar g = p.getGrammar();

  @Test
  public void test() {
    AstNode fileNode = p.parse(new File("src/test/resources/queries/collapsible_if.mc"));
    List<AstNode> ifStatements = fileNode.getDescendants(g.ifStatement);

    init();

    int violations = 0;
    for (AstNode node : ifStatements) {
      if (visitHelpers(node)) {
        violations++;
      }
    }
    assertThat(violations).isEqualTo(2);
  }

  AstQuery hasIfStatementWithoutElseQuery;
  AstQuery compoundStatementQuery;
  AstQuery hasElseChildQuery; // inline
  AstQuery childrenQuery; // inline

  private void init() {
    hasIfStatementWithoutElseQuery = new AstQuery()
        .children(g.statement)
        .children(g.ifStatement)
        .notHaving(new AstQuery().children(g.elseClause));

    compoundStatementQuery = new AstQuery()
        .children(g.statement)
        .children(g.compoundStatement);

    hasElseChildQuery = new AstQuery().children(g.elseClause);
    childrenQuery = new AstQuery().children();
  }

  /* New, compact version */

  boolean visitCompact(AstNode node) {
    if (node.select(new AstQuery().children(g.elseClause)).isEmpty()) { // TODO use compact
      AstResultSet compoundStatement = node.select(compoundStatementQuery);

      if (!node.select(hasIfStatementWithoutElseQuery).isEmpty() ||
        compoundStatement.select(new AstQuery().children()).hasSize(3) && // TODO use compact
        !compoundStatement.select(hasIfStatementWithoutElseQuery).isEmpty()) {

        return true;
      }
    }
    return false;
  }

  /* New with helpers */

  boolean visitHelpers(AstNode node) {
    return !hasElseClause(node) && hasCollapsibleIfStatement(node);
  }

  private boolean hasElseClause(AstNode node) {
    return !node.select(hasElseChildQuery).isEmpty(); // TODO use compact
  }

  private boolean hasCollapsibleIfStatement(AstNode node) {
    return hasCollapsibleInnerIfStatement(node) || hasCollapsibleIfStatementInCompoundStatement(node);
  }

  private boolean hasCollapsibleInnerIfStatement(AstNode node) {
    return !node.select(hasIfStatementWithoutElseQuery).isEmpty();
  }

  private boolean hasCollapsibleIfStatementInCompoundStatement(AstNode node) {
    AstResultSet compoundStatement = node.select(compoundStatementQuery);

    return compoundStatement.select(childrenQuery).hasSize(3) && // TODO use compact
      !compoundStatement.select(hasIfStatementWithoutElseQuery).isEmpty();
  }

}
