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

public class CopyOfCollapsibleIfQueryOldTest {

  Parser<MiniCGrammar> p = MiniCParser.create();
  MiniCGrammar g = p.getGrammar();

  @Test
  public void test() {
    AstNode fileNode = p.parse(new File("src/test/resources/queries/collapsible_if.mc"));
    List<AstNode> ifStatements = fileNode.getDescendants(g.ifStatement);

    init();

    for (AstNode node : ifStatements) {
      if (visit(node)) {
        System.out.println("Collapsible if statement at line " + node.getTokenLine());
      }
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

  /* Old with helpers */

  private boolean visit(AstNode node) {
    return !hasElseClause(node) && hasCollapsibleIfStatement(node);
  }

  private boolean hasElseClause(AstNode node) {
    return node.hasDirectChildren(g.elseClause);
  }

  private boolean hasCollapsibleIfStatement(AstNode node) {
    AstNode statementNode = node.getFirstChild(g.statement).getChild(0);
    return isCollapsibleInnerIfStatement(statementNode) || isCollapsibleIfStatementInCompoundStatement(statementNode);
  }

  private boolean isCollapsibleInnerIfStatement(AstNode node) {
    return node.is(g.ifStatement) && !hasElseClause(node);
  }

  private boolean isCollapsibleIfStatementInCompoundStatement(AstNode node) {
    if (!node.is(g.compoundStatement) || node.getNumberOfChildren() != 3) {
      return false;
    }

    AstNode statementNode = node.getFirstChild(g.statement);
    if (statementNode == null) {
      // Null check was initially forgotten, did not led to a NPE because the unit test did not cover that case yet!
      return false;
    }

    return isCollapsibleInnerIfStatement(statementNode.getChild(0));
  }

}
