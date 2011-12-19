/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;

public class TokenUtils {

  public static String merge(List<Token> tokens) {
    removeLastTokenIfEof(tokens);
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

  private static void removeLastTokenIfEof(List<Token> tokens) {
    if ( !tokens.isEmpty()) {
      Token lastToken = tokens.get(tokens.size() - 1);
      if ("EOF".equals(lastToken.getValue())) {
        tokens.remove(tokens.size() - 1);
      }
    }
  }

  public static List<Token> lex(String sourceCode) {
    List<Token> tokens = new ArrayList<Token>();
    CodeReader reader = new CodeReader(sourceCode);
    Matcher matcher = Pattern.compile("[a-zA-Z_0-9\\+\\-\\*/]+").matcher("");

    while (reader.peek() != -1) {
      StringBuilder nextStringToken = new StringBuilder();
      Token token;
      int linePosition = reader.getLinePosition();
      int columnPosition = reader.getColumnPosition();
      if (reader.popTo(matcher, nextStringToken) != -1) {
        if ("EOF".equals(nextStringToken.toString())) {
          token = new Token(GenericTokenType.EOF, nextStringToken.toString(), linePosition, columnPosition);
        } else {
          token = new Token(GenericTokenType.IDENTIFIER, nextStringToken.toString(), linePosition, columnPosition);
        }
      } else if (Character.isWhitespace(reader.peek())) {
        reader.pop();
        continue;
      } else {
        token = new Token(GenericTokenType.IDENTIFIER, "" + (char) reader.pop(), linePosition, columnPosition);
      }
      tokens.add(token);
    }
    return tokens;
  }
}
