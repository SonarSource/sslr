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
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.sonar.sslr.internal.matchers.InputBuffer.Position;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public final class AstCreator {

  private final char[] input;
  private final InputBuffer inputBuffer;
  private final Token.Builder tokenBuilder = Token.builder();
  private final List<Trivia> trivias = Lists.newArrayList();

  public static AstNode create(URI uri, char[] input, ParseNode node) {
    return new AstCreator(uri, input).visit(node);
  }

  private AstCreator(URI uri, char[] input) {
    this.input = input;
    this.inputBuffer = new InputBuffer(input);
    tokenBuilder.setURI(uri);
  }

  private AstNode visit(ParseNode node) {
    if (node.getChildren().isEmpty()) {
      return visitTerminal(node);
    } else {
      return visitNonTerminal(node);
    }
  }

  private AstNode visitTerminal(ParseNode node) {
    if (!(node.getMatcher() instanceof TokenMatcher)) {
      return null;
    }
    TokenMatcher ruleMatcher = (TokenMatcher) node.getMatcher();

    Position position = inputBuffer.getPosition(node.getStartIndex());
    tokenBuilder.setLine(position.getLine());
    tokenBuilder.setColumn(position.getColumn() - 1);

    String value = getValue(node);

    tokenBuilder.setValueAndOriginalValue(value).setType(ruleMatcher.getTokenType());
    if (ruleMatcher.getTokenType() == GenericTokenType.COMMENT) {
      tokenBuilder.setTrivia(Collections.EMPTY_LIST);
      trivias.add(Trivia.createComment(tokenBuilder.build()));
      return null;
    }
    Token token = tokenBuilder.setTrivia(trivias).build();
    trivias.clear();
    AstNode astNode = new AstNode(token);
    astNode.setFromIndex(node.getStartIndex());
    astNode.setToIndex(node.getEndIndex());
    return astNode;
  }

  private AstNode visitNonTerminal(ParseNode node) {
    GrammarElementMatcher ruleMatcher = (GrammarElementMatcher) node.getMatcher();
    List<AstNode> astNodes = Lists.newArrayList();
    for (ParseNode child : node.getChildren()) {
      AstNode astNode = visit(child);
      if (astNode != null) {
        if (astNode.hasToBeSkippedFromAst()) {
          astNodes.addAll(astNode.getChildren());
        } else {
          astNodes.add(astNode);
        }
      }
    }

    Token token = astNodes.isEmpty() ? null : astNodes.get(0).getToken();
    AstNode astNode = new AstNode(ruleMatcher, ruleMatcher.getName(), token);
    for (AstNode child : astNodes) {
      astNode.addChild(child);
    }
    astNode.setFromIndex(node.getStartIndex());
    astNode.setToIndex(node.getEndIndex());
    return astNode;
  }

  private String getValue(ParseNode node) {
    StringBuilder result = new StringBuilder();
    for (int i = node.getStartIndex(); i < Math.min(node.getEndIndex(), input.length); i++) {
      result.append(inputBuffer.charAt(i));
    }
    return result.toString();
  }

}
