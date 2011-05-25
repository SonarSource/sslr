/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.parser;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

class NotParseMatcher extends BaseMatcher<Parser> {

  private final String sourceCode;

  public NotParseMatcher(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof Parser)) {
      return false;
    }
    Parser parser = (Parser) obj;

    try {
      parser.parse(sourceCode);
    } catch (RecognitionExceptionImpl e) {
      return true;
    }
    if (parser.getParsingState().hasNextToken()
        && parser.getParsingState().readToken(parser.getParsingState().lexerIndex).getType() != GenericTokenType.EOF) {
      return true;
    }
    return false;
  }

  public void describeTo(Description desc) {
    desc.appendText("All tokens have been consumed '" + sourceCode + "'");
  }
}
