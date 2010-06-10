package com.sonar.sslr.test.parser;

/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;

class NotMatchMatcher extends BaseMatcher<Matcher> {

  private final String sourceCode;
  private final Lexer lexer;

  public NotMatchMatcher(String sourceCode, Lexer lexer) {
    this.sourceCode = sourceCode;
    this.lexer = lexer;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof Matcher)) {
      return false;
    }
    Matcher matcherUnderTest = (Matcher) obj;
    ParsingState parsingState = new ParsingState(lexer.lex(sourceCode).getTokens());
    try {
      matcherUnderTest.match(parsingState);
      return false;
    } catch (RecognitionExceptionImpl e) {
      return true;
    }
  }

  public void describeTo(Description desc) {
    desc.appendText("The SSLR matcher matches the beginining of '" + sourceCode + "'");
  }
}
