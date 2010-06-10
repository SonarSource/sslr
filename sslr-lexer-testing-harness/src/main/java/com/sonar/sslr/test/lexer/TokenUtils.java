package com.sonar.sslr.test.lexer;

import java.util.List;

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
    Token lastToken = tokens.get(tokens.size() - 1);
    if (lastToken.getValue().equals("EOF")) {
      tokens.remove(tokens.size() - 1);
    }
  }
}
