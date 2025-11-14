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
package com.sonar.sslr.impl.channel;

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;

public class IdentifierAndKeywordChannel extends Channel<Lexer> {

  private final Map<String, TokenType> keywordsMap = new HashMap<>();
  private final StringBuilder tmpBuilder = new StringBuilder();
  private final Matcher matcher;
  private final boolean caseSensitive;
  private final Token.Builder tokenBuilder = Token.builder();

  /**
   * @throws java.util.regex.PatternSyntaxException if the expression's syntax is invalid
   */
  public IdentifierAndKeywordChannel(String regexp, boolean caseSensitive, TokenType[]... keywordSets) {
    for (TokenType[] keywords : keywordSets) {
      for (TokenType keyword : keywords) {
        String keywordValue = caseSensitive ? keyword.getValue() : keyword.getValue().toUpperCase();
        keywordsMap.put(keywordValue, keyword);
      }
    }
    this.caseSensitive = caseSensitive;
    matcher = Pattern.compile(regexp).matcher("");
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    if (code.popTo(matcher, tmpBuilder) > 0) {
      String word = tmpBuilder.toString();
      String wordOriginal = word;
      if (!caseSensitive) {
        word = word.toUpperCase();
      }

      TokenType keywordType = keywordsMap.get(word);
      Token token = tokenBuilder
          .setType(keywordType == null ? IDENTIFIER : keywordType)
          .setValueAndOriginalValue(word, wordOriginal)
          .setURI(lexer.getURI())
          .setLine(code.getPreviousCursor().getLine())
          .setColumn(code.getPreviousCursor().getColumn())
          .build();

      lexer.addToken(token);

      tmpBuilder.delete(0, tmpBuilder.length());
      return true;
    }
    return false;
  }

}
