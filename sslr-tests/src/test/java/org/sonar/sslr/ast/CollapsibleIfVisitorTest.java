/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
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

public class CollapsibleIfVisitorTest {

  private final Parser<Grammar> p = MiniCParser.create();
  private final Grammar g = p.getGrammar();

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
    return !hasElseClause(node) && hasCollapsibleIfStatement(node);
  }

  private boolean hasElseClause(AstNode node) {
    return node.hasDirectChildren(MiniCGrammar.ELSE_CLAUSE);
  }

  private boolean hasCollapsibleIfStatement(AstNode node) {
    AstNode statementNode = node.getFirstChild(MiniCGrammar.STATEMENT).getFirstChild();
    return isIfStatementWithoutElse(statementNode) || isIfStatementWithoutElseInCompoundStatement(statementNode);
  }

  private boolean isIfStatementWithoutElse(AstNode node) {
    return node.is(MiniCGrammar.IF_STATEMENT) && !hasElseClause(node);
  }

  private boolean isIfStatementWithoutElseInCompoundStatement(AstNode node) {
    if (!node.is(MiniCGrammar.COMPOUND_STATEMENT) || node.getNumberOfChildren() != 3) {
      return false;
    }
    AstNode statementNode = node.getFirstChild(MiniCGrammar.STATEMENT);
    if (statementNode == null) {
      // Null check was initially forgotten, did not led to a NPE because the unit test did not cover that case yet!
      return false;
    }
    return isIfStatementWithoutElse(statementNode.getFirstChild());
  }

}
