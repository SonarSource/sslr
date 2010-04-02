/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import java.util.ArrayList;
import java.util.List;

import com.sonarsource.sslr.MockTokenType;
import com.sonarsource.sslr.ParsingStackTrace;
import com.sonarsource.sslr.ParsingState;
import com.sonarsource.sslr.RecognitionExceptionImpl;
import com.sonarsource.sslr.api.Token;
import com.sonarsource.sslr.matcher.Matcher;

public class MatcherCase {

  protected void assertMatch(Matcher matcher, String... tokens) {
    ParsingState parsingState = new ParsingState(convertStringsToTokens(tokens));
    try {
      matcher.match(parsingState);
    } catch (RecognitionExceptionImpl e) {
      throw new AssertionError(ParsingStackTrace.generate(parsingState));
    }
    if (parsingState.hasNextToken()) {
      throw new AssertionError("The tokens" + arrayToString(tokens) + " haven't been all consumed.");
    }
  }

  private List<Token> convertStringsToTokens(String[] strings) {
    List<Token> tokens = new ArrayList<Token>();
    for (String value : strings) {
      tokens.add(new Token(MockTokenType.WORD, value));
    }
    return tokens;
  }

  protected void assertNotMatch(Matcher matcher, String... tokens) {
    ParsingState parsingState = new ParsingState(convertStringsToTokens(tokens));
    try {
      matcher.match(parsingState);
    } catch (RecognitionExceptionImpl e) {
    }
    if (!parsingState.hasNextToken()) {
      throw new AssertionError("The tokens" + arrayToString(tokens) + "  have been all consumed.");
    }
  }

  private String arrayToString(String... tokens) {
    StringBuilder result = new StringBuilder("(");
    for (int i = 0; i < tokens.length; i++) {
      String token = tokens[i];
      result.append(token);
      if (i < tokens.length - 1) {
        result.append(",");
      }
    }
    result.append(")");
    return result.toString();
  }

}
