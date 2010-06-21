package com.sonar.sslr.test.parser;

/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.ParsingStackTrace;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;

class MatchMatcher extends BaseMatcher<Matcher> {

  private final String sourceCode;
  private final Lexer lexer;
  private String parsingStackTrace;
  private Matcher matcher;

  public MatchMatcher(String sourceCode, Lexer lexer) {
    this.sourceCode = sourceCode;
    this.lexer = lexer;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof Matcher)) {
      return false;
    }
    matcher = (Matcher) obj;
    ParsingState parsingState = new ParsingState(lexer.lex(sourceCode).getTokens());
    try {
      matcher.match(parsingState);
      return true;
    } catch (RecognitionExceptionImpl e) {
      parsingStackTrace = ParsingStackTrace.generateFullStackTrace(parsingState);
      return false;
    }
  }

  public void describeTo(Description desc) {
    if (parsingStackTrace != null) {
      desc.appendText("The matcher '" + matcher + "' doesn't match the beginning of '" + sourceCode + "'.\n");
      desc.appendText("Parsing stack trace : " + parsingStackTrace);
    } else {
      desc.appendText("The matcher '" + matcher + "' hasn't matched the overall expression '" + sourceCode + "'.\n");
    }
  }
}
