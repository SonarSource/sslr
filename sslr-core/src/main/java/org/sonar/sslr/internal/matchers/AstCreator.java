/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.internal.matchers;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.vm.TokenExpression;
import org.sonar.sslr.internal.vm.TriviaExpression;
import org.sonar.sslr.parser.ParsingResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AstCreator {

  private static final URI FAKE_URI;

  static {
    try {
      FAKE_URI = new URI("tests://unittest");
    } catch (URISyntaxException e) {
      // Can't happen
      throw new IllegalStateException(e);
    }
  }

  private final LocatedText input;
  private final Token.Builder tokenBuilder = Token.builder();
  private final List<Trivia> trivias = new ArrayList<>();

  public static AstNode create(ParsingResult parsingResult, LocatedText input) {
    AstNode astNode = new AstCreator(input).visit(parsingResult.getParseTreeRoot());
    // Unwrap AstNodeType for root node:
    astNode.hasToBeSkippedFromAst();
    return astNode;
  }

  private AstCreator(LocatedText input) {
    this.input = input;
  }

  private AstNode visit(ParseNode node) {
    if (node.getMatcher() instanceof MutableParsingRule) {
      return visitNonTerminal(node);
    } else {
      return visitTerminal(node);
    }
  }

  private AstNode visitTerminal(ParseNode node) {
    if (node.getMatcher() instanceof TriviaExpression) {
      TriviaExpression ruleMatcher = (TriviaExpression) node.getMatcher();
      if (ruleMatcher.getTriviaKind() == TriviaKind.SKIPPED_TEXT) {
        return null;
      } else if (ruleMatcher.getTriviaKind() == TriviaKind.COMMENT) {
        updateTokenPositionAndValue(node);
        tokenBuilder.setTrivia(Collections.<Trivia>emptyList());
        tokenBuilder.setType(GenericTokenType.COMMENT);
        trivias.add(Trivia.createComment(tokenBuilder.build()));
        return null;
      } else {
        throw new IllegalStateException("Unexpected trivia kind: " + ruleMatcher.getTriviaKind());
      }
    } else if (node.getMatcher() instanceof TokenExpression) {
      updateTokenPositionAndValue(node);
      TokenExpression ruleMatcher = (TokenExpression) node.getMatcher();
      tokenBuilder.setType(ruleMatcher.getTokenType());
      if (ruleMatcher.getTokenType() == GenericTokenType.COMMENT) {
        tokenBuilder.setTrivia(Collections.<Trivia>emptyList());
        trivias.add(Trivia.createComment(tokenBuilder.build()));
        return null;
      }
    } else {
      updateTokenPositionAndValue(node);
      tokenBuilder.setType(UNDEFINED_TOKEN_TYPE);
    }
    Token token = tokenBuilder.setTrivia(trivias).build();
    trivias.clear();
    AstNode astNode = new AstNode(token);
    astNode.setFromIndex(node.getStartIndex());
    astNode.setToIndex(node.getEndIndex());
    return astNode;
  }

  private void updateTokenPositionAndValue(ParseNode node) {
    TextLocation location = input.getLocation(node.getStartIndex());
    if (location == null) {
      tokenBuilder.setGeneratedCode(true);
      // Godin: line, column and uri has no value for generated code, but we should bypass checks in TokenBuilder
      tokenBuilder.setLine(1);
      tokenBuilder.setColumn(0);
      tokenBuilder.setURI(FAKE_URI);
    } else {
      tokenBuilder.setGeneratedCode(false);
      tokenBuilder.setLine(location.getLine());
      tokenBuilder.setColumn(location.getColumn() - 1);
      tokenBuilder.setURI(location.getFileURI() == null ? FAKE_URI : location.getFileURI());
      tokenBuilder.notCopyBook();
    }

    String value = getValue(node);
    tokenBuilder.setValueAndOriginalValue(value);
  }

  private AstNode visitNonTerminal(ParseNode node) {
    MutableParsingRule ruleMatcher = (MutableParsingRule) node.getMatcher();
    List<AstNode> astNodes = new ArrayList<>();
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

    Token token = null;
    for (AstNode child : astNodes) {
      if (child.getToken() != null) {
        token = child.getToken();
        break;
      }
    }

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
    for (int i = node.getStartIndex(); i < Math.min(node.getEndIndex(), input.length()); i++) {
      result.append(input.charAt(i));
    }
    return result.toString();
  }

  // @VisibleForTesting
  static final TokenType UNDEFINED_TOKEN_TYPE = new TokenType() {
    @Override
    public String getName() {
      return "TOKEN";
    }

    @Override
    public String getValue() {
      return getName();
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }
  };

}
