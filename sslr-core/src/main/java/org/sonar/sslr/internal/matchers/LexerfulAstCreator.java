/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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
package org.sonar.sslr.internal.matchers;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.sonar.sslr.internal.vm.lexerful.TokenTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class LexerfulAstCreator {

  public static AstNode create(ParseNode node, List<Token> tokens) {
    AstNode astNode = new LexerfulAstCreator(tokens).visit(node);
    // Unwrap AstNodeType for root node:
    astNode.hasToBeSkippedFromAst();
    return astNode;
  }

  private final List<Token> tokens;

  private LexerfulAstCreator(List<Token> tokens) {
    this.tokens = tokens;
  }

  private AstNode visit(ParseNode node) {
    if (node.getMatcher() instanceof RuleDefinition) {
      return visitNonTerminal(node);
    } else {
      return visitTerminal(node);
    }
  }

  private AstNode visitNonTerminal(ParseNode node) {
    List<AstNode> astNodes = new ArrayList<>();
    for (ParseNode child : node.getChildren()) {
      AstNode astNode = visit(child);
      if (astNode == null) {
        // skip
      } else if (astNode.hasToBeSkippedFromAst()) {
        astNodes.addAll(astNode.getChildren());
      } else {
        astNodes.add(astNode);
      }
    }

    RuleDefinition ruleMatcher = (RuleDefinition) node.getMatcher();

    Token token = node.getStartIndex() < tokens.size() ? tokens.get(node.getStartIndex()) : null;
    AstNode astNode = new AstNode(ruleMatcher, ruleMatcher.getName(), token);
    for (AstNode child : astNodes) {
      astNode.addChild(child);
    }
    astNode.setFromIndex(node.getStartIndex());
    astNode.setToIndex(node.getEndIndex());

    return astNode;
  }

  private AstNode visitTerminal(ParseNode node) {
    Token token = tokens.get(node.getStartIndex());
    // For compatibility with SSLR < 1.19, TokenType should be checked only for TokenTypeExpression:
    if ((node.getMatcher() instanceof TokenTypeExpression) && token.getType().hasToBeSkippedFromAst(null)) {
      return null;
    }
    AstNode astNode = new AstNode(token);
    astNode.setFromIndex(node.getStartIndex());
    astNode.setToIndex(node.getEndIndex());
    return astNode;
  }

}
