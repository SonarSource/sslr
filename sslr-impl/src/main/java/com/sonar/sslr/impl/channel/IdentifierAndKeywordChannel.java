/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import java.util.HashMap;
import java.util.Map;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.LexerOutput;

public class IdentifierAndKeywordChannel implements Channel<LexerOutput> {

  private final Map<String, TokenType> keywordsMap;

  public IdentifierAndKeywordChannel(TokenType... keywords) {
    keywordsMap = new HashMap<String, TokenType>();
    for (TokenType keyword : keywords) {
      keywordsMap.put(keyword.getValue(), keyword);
    }
  }

  public boolean consum(CodeReader code, LexerOutput output) {
    if (Character.isJavaIdentifierStart((char) code.peek())) {
      StringBuilder wordBuilder = new StringBuilder();
      code.popTo(new EndWordMatcher(), wordBuilder);
      String word = wordBuilder.toString();
      if (isVbKeyword(word)) {
        output.addToken(keywordsMap.get(word), word, code.getLinePosition(), code.getColumnPosition());
      } else {
        output.addToken(GenericTokenType.IDENTIFIER, word, code.getLinePosition(), code.getColumnPosition());
      }
      return true;
    }
    return false;
  }

  private boolean isVbKeyword(String word) {
    return keywordsMap.containsKey(word);
  }

  private static class EndWordMatcher implements EndMatcher {

    public boolean match(int toMatch) {
      return !Character.isJavaIdentifierPart(toMatch);
    }

  }
}