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
package com.sonar.sslr.api.typed;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia;
import org.sonar.sslr.grammar.GrammarRuleKey;

import java.util.List;

/**
 * @since 1.21
 */
public class AstNodeBuilder implements NodeBuilder {

  private static final TokenType UNDEFINED_TOKEN_TYPE = new UndefinedTokenType();

  @Override
  public AstNode createNonTerminal(GrammarRuleKey ruleKey, Rule rule, List<Object> children, int startIndex, int endIndex) {
    Token token = null;

    for (Object child : children) {
      if (child instanceof AstNode && ((AstNode) child).hasToken()) {
        token = ((AstNode) child).getToken();
        break;
      }
    }
    AstNode astNode = new AstNode(rule, ruleKey.toString(), token);
    for (Object child : children) {
      astNode.addChild((AstNode) child);
    }

    astNode.setFromIndex(startIndex);
    astNode.setToIndex(endIndex);

    return astNode;
  }

  @Override
  public AstNode createTerminal(Input input, int startIndex, int endIndex, List<Trivia> trivias, TokenType type) {
    int[] lineAndColumn = input.lineAndColumnAt(startIndex);
    Token token = Token.builder()
      .setType(type == null ? UNDEFINED_TOKEN_TYPE : type)
      .setLine(lineAndColumn[0])
      .setColumn(lineAndColumn[1] - 1)
      .setValueAndOriginalValue(input.substring(startIndex, endIndex))
      .setURI(input.uri())
      .setGeneratedCode(false)
      .setTrivia(trivias)
      .build();
    AstNode astNode = new AstNode(token);
    astNode.setFromIndex(startIndex);
    astNode.setToIndex(endIndex);
    return astNode;
  }

  private static final class UndefinedTokenType implements TokenType {

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

    @Override
    public String toString() {
      return UndefinedTokenType.class.getSimpleName();
    }
  }

}
