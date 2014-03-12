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
package com.sonar.sslr.impl.channel;

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import com.google.common.collect.ImmutableMap;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;

public class IdentifierAndKeywordChannel extends Channel<Lexer> {

  private final Map<String, TokenType> keywordsMap;
  private final StringBuilder tmpBuilder = new StringBuilder();
  private final Matcher matcher;
  private final boolean caseSensitive;
  private final Token.Builder tokenBuilder = Token.builder();

  /**
   * @throws java.util.regex.PatternSyntaxException if the expression's syntax is invalid
   */
  public IdentifierAndKeywordChannel(String regexp, boolean caseSensitive, TokenType[]... keywordSets) {
    ImmutableMap.Builder<String, TokenType> keywordsMapBuilder = ImmutableMap.builder();
    for (TokenType[] keywords : keywordSets) {
      for (TokenType keyword : keywords) {
        String keywordValue = caseSensitive ? keyword.getValue() : keyword.getValue().toUpperCase();
        keywordsMapBuilder.put(keywordValue, keyword);
      }
    }
    this.keywordsMap = keywordsMapBuilder.build();
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
