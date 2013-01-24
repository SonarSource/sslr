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
import org.sonar.sslr.internal.ast.select.AstSelect;
import org.sonar.sslr.internal.ast.select.AstSelectFactory;

import java.io.File;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class CollapsibleIfSelectTest {

  private Parser<MiniCGrammar> p = MiniCParser.create();
  private MiniCGrammar g = p.getGrammar();

  @Test
  public void test() {
    AstNode fileNode = p.parse(new File("src/test/resources/queries/collapsible_if.mc"));
    List<AstNode> ifStatements = fileNode.getDescendants(g.ifStatement);

    int violations = 0;
    for (AstNode node : ifStatements) {
      if (visit(node)) {
        violations++;
      }
    }
    assertThat(violations).isEqualTo(2);
  }

  private boolean visit(AstNode node) {
    AstSelect select = AstSelectFactory.select(node);
    return !hasElseClause(select)
      && hasCollapsibleIfStatement(select);
  }

  private boolean hasElseClause(AstSelect select) {
    return select.children(g.elseClause).size() != 0;
  }

  private boolean hasCollapsibleIfStatement(AstSelect select) {
    return hasCollapsibleInnerIfStatement(select)
      || hasCollapsibleIfStatementInCompoundStatement(select);
  }

  private boolean hasCollapsibleInnerIfStatement(AstSelect select) {
    return !hasIfStatementWithoutElse(select);
  }

  private boolean hasCollapsibleIfStatementInCompoundStatement(AstSelect select) {
    AstSelect compoundStatement = select
        .children(g.statement)
        .children(g.compoundStatement);
    return compoundStatement.children().size() == 3
      && !hasIfStatementWithoutElse(compoundStatement);
  }

  private boolean hasIfStatementWithoutElse(AstSelect select) {
    return select
        .children(g.statement)
        .children(g.ifStatement)
        .children(g.elseClause)
        .isEmpty();
  }

}
