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
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.matcher.MemoizedMatcher;

class MatchMatcher extends BaseMatcher<MemoizedMatcher> {

  private final String sourceCode;
  private final Lexer lexer;
  private String parsingStackTrace;
  private MemoizedMatcher matcher;

  public MatchMatcher(String sourceCode, Lexer lexer) {
    this.sourceCode = sourceCode;
    this.lexer = lexer;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof MemoizedMatcher)) {
      return false;
    }
    matcher = (MemoizedMatcher) obj;
    ParsingState parsingState = new ParsingState(lexer.lex(sourceCode).getTokens());
    try {
      matcher.match(parsingState);
      return true;
    } catch (BacktrackingException e) {
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
