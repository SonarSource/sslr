/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.test.lexer;

import com.sonar.sslr.api.Token;
import org.sonar.sslr.channel.CodeReader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.test.lexer.MockHelper.mockTokenBuilder;

public final class TokenUtils {

  private TokenUtils() {
  }

  public static String merge(List<Token> tokens) {
    tokens = removeLastTokenIfEof(tokens);
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      result.append(token.getValue());
      if (i < tokens.size() - 1) {
        result.append(" ");
      }
    }
    return result.toString();
  }

  private static List<Token> removeLastTokenIfEof(List<Token> tokens) {
    if ( !tokens.isEmpty()) {
      Token lastToken = tokens.get(tokens.size() - 1);
      if ("EOF".equals(lastToken.getValue())) {
        return tokens.subList(0, tokens.size() - 1);
      }
    }

    return tokens;
  }

  public static List<Token> lex(String sourceCode) {
    List<Token> tokens = new ArrayList<>();
    CodeReader reader = new CodeReader(sourceCode);
    Matcher matcher = Pattern.compile("[a-zA-Z_0-9\\+\\-\\*/]+").matcher("");

    while (reader.peek() != -1) {
      StringBuilder nextStringToken = new StringBuilder();
      Token token;
      int linePosition = reader.getLinePosition();
      int columnPosition = reader.getColumnPosition();
      if (reader.popTo(matcher, nextStringToken) != -1) {
        if ("EOF".equals(nextStringToken.toString())) {
          token = mockTokenBuilder(EOF, nextStringToken.toString()).setLine(linePosition).setColumn(columnPosition).build();
        } else {
          token = mockTokenBuilder(IDENTIFIER, nextStringToken.toString()).setLine(linePosition).setColumn(columnPosition).build();
        }
      } else if (Character.isWhitespace(reader.peek())) {
        reader.pop();
        continue;
      } else {
        token = mockTokenBuilder(IDENTIFIER, Character.toString((char) reader.pop())).setLine(linePosition).setColumn(columnPosition).build();
      }
      tokens.add(token);
    }
    return tokens;
  }

}
