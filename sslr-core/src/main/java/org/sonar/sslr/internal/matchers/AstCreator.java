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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.*;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.matchers.InputBuffer.Position;
import org.sonar.sslr.parser.ParsingResult;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public final class AstCreator {

  private final InputBuffer inputBuffer;
  private final Token.Builder tokenBuilder = Token.builder();
  private final List<Trivia> trivias = Lists.newArrayList();

  public static AstNode create(URI uri, ParsingResult parsingResult) {
    return new AstCreator(uri, parsingResult.getInputBuffer()).visit(parsingResult.getParseTreeRoot());
  }

  private AstCreator(URI uri, InputBuffer inputBuffer) {
    this.inputBuffer = inputBuffer;
    tokenBuilder.setURI(uri);
  }

  private AstNode visit(ParseNode node) {
    if (node.getMatcher() instanceof GrammarElementMatcher) {
      return visitNonTerminal(node);
    } else {
      return visitTerminal(node);
    }
  }

  private AstNode visitTerminal(ParseNode node) {
    Position position = inputBuffer.getPosition(node.getStartIndex());
    tokenBuilder.setLine(position.getLine());
    tokenBuilder.setColumn(position.getColumn() - 1);

    String value = getValue(node);
    tokenBuilder.setValueAndOriginalValue(value);
    if (node.getMatcher() instanceof TriviaMatcher) {
      TriviaMatcher ruleMatcher = (TriviaMatcher) node.getMatcher();
      if (ruleMatcher.getTriviaKind() == TriviaKind.SKIPPED_TEXT) {
        return null;
      } else if (ruleMatcher.getTriviaKind() == TriviaKind.COMMENT) {
        tokenBuilder.setTrivia(Collections.EMPTY_LIST);
        tokenBuilder.setType(GenericTokenType.COMMENT);
        trivias.add(Trivia.createComment(tokenBuilder.build()));
        return null;
      } else {
        throw new IllegalStateException("Unexpected trivia kind: " + ruleMatcher.getTriviaKind());
      }
    } else if (node.getMatcher() instanceof TokenMatcher) {
      TokenMatcher ruleMatcher = (TokenMatcher) node.getMatcher();
      tokenBuilder.setType(ruleMatcher.getTokenType());
      if (ruleMatcher.getTokenType() == GenericTokenType.COMMENT) {
        tokenBuilder.setTrivia(Collections.EMPTY_LIST);
        trivias.add(Trivia.createComment(tokenBuilder.build()));
        return null;
      }
    } else {
      tokenBuilder.setType(UNDEFINED_TOKEN_TYPE);
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
    for (int i = node.getStartIndex(); i < Math.min(node.getEndIndex(), inputBuffer.length()); i++) {
      result.append(inputBuffer.charAt(i));
    }
    return result.toString();
  }

  @VisibleForTesting
  static TokenType UNDEFINED_TOKEN_TYPE = new TokenType() {
    public String getName() {
      return "TOKEN";
    }

    public String getValue() {
      return getName();
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }
  };

}
