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
package org.sonar.sslr.internal.matchers;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleDefinition;

import java.util.List;

public class LexerfulAstCreator {

  public static AstNode create(ParseNode node, List<Token> tokens) {
    return new LexerfulAstCreator(tokens).visit(node);
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
    List<AstNode> astNodes = Lists.newArrayList();
    for (ParseNode child : node.getChildren()) {
      AstNode astNode = visit(child);
      if (astNode.hasToBeSkippedFromAst()) {
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
    return new AstNode(tokens.get(node.getStartIndex()));
  }

}
