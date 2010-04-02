/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr.impl.matcher;

import java.util.ArrayList;
import java.util.List;

import com.sonarsource.sslr.api.Token;
import com.sonarsource.sslr.impl.MockTokenType;

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
