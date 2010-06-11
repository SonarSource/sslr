/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.parser;

import org.hamcrest.Matcher;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.Parser;

public class ParserMatchers {

  public final static Matcher<com.sonar.sslr.impl.matcher.Matcher> match(String sourceCode, Lexer lexer) {
    return new MatchMatcher(sourceCode, lexer);
  }

  public final static Matcher<Parser> parse(String sourceCode) {
    return new ParseMatcher(sourceCode);
  }

  public final static Matcher<Parser> notParse(String sourceCode) {
    return new NotParseMatcher(sourceCode);
  }
}
