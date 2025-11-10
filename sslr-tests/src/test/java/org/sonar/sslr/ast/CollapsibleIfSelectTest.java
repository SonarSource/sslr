/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.ast;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.minic.MiniCGrammar;
import com.sonar.sslr.test.minic.MiniCParser;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

public class CollapsibleIfSelectTest {

  private Parser<Grammar> p = MiniCParser.create();
  private Grammar g = p.getGrammar();

  @Test
  public void test() {
    AstNode fileNode = p.parse(new File("src/test/resources/queries/collapsible_if.mc"));
    List<AstNode> ifStatements = fileNode.getDescendants(MiniCGrammar.IF_STATEMENT);

    Set<Integer> violations = new HashSet<>();
    for (AstNode node : ifStatements) {
      if (visit(node)) {
        violations.add(node.getTokenLine());
      }
    }
    assertThat(violations).containsOnly(7, 16);
  }

  private boolean visit(AstNode node) {
    AstSelect select = node.select();
    return hasNoElseClause(select) && (hasIfStatementWithoutElse(select) || hasIfStatementWithoutElseInCompoundStatement(select));
  }

  private boolean hasNoElseClause(AstSelect select) {
    return select.children(MiniCGrammar.ELSE_CLAUSE).isEmpty();
  }

  private boolean hasIfStatementWithoutElseInCompoundStatement(AstSelect select) {
    select = select
        .children(MiniCGrammar.STATEMENT)
        .children(MiniCGrammar.COMPOUND_STATEMENT);
    return select.children().size() == 3
      && hasIfStatementWithoutElse(select);
  }

  private boolean hasIfStatementWithoutElse(AstSelect select) {
    select = select.children(MiniCGrammar.STATEMENT).children(MiniCGrammar.IF_STATEMENT);
    return select.isNotEmpty() && hasNoElseClause(select);
  }

}
