/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sonar.channel.ChannelDispatcher;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.MockTokenType;

public class WordLexer extends Lexer {

  public LexerOutput lex(String source) {
    List<Token> tokens = new ArrayList<Token>();
    for (String wordToken : source.split(" ")) {
      Token token = new Token(MockTokenType.WORD, wordToken);
      tokens.add(token);
    }
    LexerOutput output = new LexerOutput();
    output.addAllTokens(tokens);
    return output;
  }

  @Override
  public LexerOutput lex(File file) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected ChannelDispatcher<LexerOutput> getChannelDispatcher() {
    // TODO Auto-generated method stub
    return null;
  }
}
