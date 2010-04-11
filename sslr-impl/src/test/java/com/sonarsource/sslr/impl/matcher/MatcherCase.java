/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl.matcher;

import java.util.ArrayList;
import java.util.List;

import com.sonarsource.sslr.api.Token;
import com.sonarsource.sslr.impl.LexerOutput;
import com.sonarsource.sslr.impl.MockTokenType;
import com.sonarsource.sslr.impl.ParsingStackTrace;
import com.sonarsource.sslr.impl.ParsingState;
import com.sonarsource.sslr.impl.RecognitionExceptionImpl;

public class MatcherCase {

  protected void assertMatch(Matcher matcher, String... tokens) {
    ParsingState parsingState = new ParsingState(convertStringsToLexerOutput(tokens));
    try {
      matcher.match(parsingState);
    } catch (RecognitionExceptionImpl e) {
      throw new AssertionError(ParsingStackTrace.generate(parsingState));
    }
    if (parsingState.hasNextToken()) {
      throw new AssertionError("The tokens" + arrayToString(tokens) + " haven't been all consumed.");
    }
  }

  private LexerOutput convertStringsToLexerOutput(String[] strings) {
    List<Token> tokens = new ArrayList<Token>();
    for (String value : strings) {
      tokens.add(new Token(MockTokenType.WORD, value));
    }
    LexerOutput output = new LexerOutput();
    output.addAllTokens(tokens);
    return output;
  }

  protected void assertNotMatch(Matcher matcher, String... tokens) {
    ParsingState parsingState = new ParsingState(convertStringsToLexerOutput(tokens));
    try {
      matcher.match(parsingState);
    } catch (RecognitionExceptionImpl e) {
    }
    if ( !parsingState.hasNextToken()) {
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
