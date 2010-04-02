/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.parser.matcher;

import java.util.ArrayList;
import java.util.List;

import com.sonarsource.parser.MockTokenType;
import com.sonarsource.sslr.api.Token;

public class WordLexer {

  public List<Token> lex(String source) {
    List<Token> tokens = new ArrayList<Token>();
    for (String wordToken : source.split(" ")) {
      Token token = new Token(MockTokenType.WORD, wordToken);
      tokens.add(token);
    }
    return tokens;
  }
}
