/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import java.util.ArrayList;
import java.util.List;

import com.sonarsource.lexer.Token;
import com.sonarsource.parser.MockTokenType;
import com.sonarsource.parser.ParsingStackTrace;
import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.RecognitionException;

public class MatcherCase {

  protected void assertMatch(Matcher matcher, String... tokens) {
    ParsingState parsingState = new ParsingState(convertStringsToTokens(tokens));
    try {
      matcher.match(parsingState);
    } catch (RecognitionException e) {
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
    } catch (RecognitionException e) {
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
