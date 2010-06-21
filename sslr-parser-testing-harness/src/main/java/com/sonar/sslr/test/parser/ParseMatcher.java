/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.parser;

import static com.sonar.sslr.impl.matcher.Matchers.opt;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ParsingStackTrace;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

class ParseMatcher extends BaseMatcher<Parser> {

  private final String sourceCode;

  public ParseMatcher(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof Parser)) {
      return false;
    }
    Parser parser = (Parser) obj;
    if (parser.getRootRule() == null) {
      throw new IllegalStateException("The root rule of the parser is null. No grammar decorator seems to be activated.");
    }
    if ( !parser.getRootRule().toEBNFNotation().contains(" EOF ")) {
      parser.getRootRule().and(opt(GenericTokenType.EOF));
    }
    try {
      parser.parse(sourceCode);
    } catch (RecognitionExceptionImpl e) {
      throw new AssertionError(ParsingStackTrace.generateFullStackTrace(parser.getParsingState()));
    }
    return !parser.getParsingState().hasNextToken();
  }

  public void describeTo(Description desc) {
    desc.appendText("Tokens haven't been all consumed '" + sourceCode + "'");
  }
}
