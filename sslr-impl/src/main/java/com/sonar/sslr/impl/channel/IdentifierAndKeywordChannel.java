/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.TokenType;

public class IdentifierAndKeywordChannel implements Channel<LexerOutput> {

  private final Map<String, TokenType> keywordsMap;
  private final StringBuilder tmpBuilder = new StringBuilder();
  private final Matcher matcher;
  private final boolean caseSensitive;

  public IdentifierAndKeywordChannel(String regexp, boolean caseSensitive, TokenType... keywords) {
    keywordsMap = new HashMap<String, TokenType>();
    for (TokenType keyword : keywords) {
      String keywordValue = (caseSensitive ? keyword.getValue() : keyword.getValue().toUpperCase());
      keywordsMap.put(keywordValue, keyword);
    }
    this.caseSensitive = caseSensitive;
    matcher = Pattern.compile(regexp).matcher("");
  }

  public boolean consum(CodeReader code, LexerOutput output) {
    if (code.popTo(matcher, tmpBuilder) > 0) {
      String word = tmpBuilder.toString();
      if ( !caseSensitive) {
        word = word.toUpperCase();
      }
      if (isKeyword(word)) {
        output.addTokenAndProcess(keywordsMap.get(word), word, code.getLinePosition(), code.getColumnPosition());
      } else {
        output.addTokenAndProcess(GenericTokenType.IDENTIFIER, word, code.getLinePosition(), code.getColumnPosition());
      }
      tmpBuilder.delete(0, tmpBuilder.length());
      return true;
    }
    return false;
  }

  private boolean isKeyword(String word) {
    return keywordsMap.containsKey(word);
  }

}