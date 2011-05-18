/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.parser;

import static com.sonar.sslr.impl.matcher.Matchers.opt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.commons.lang.StringUtils;
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

    try {
      parser.parse(sourceCode);
    } catch (RecognitionExceptionImpl e) {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	parser.printStackTrace(new PrintStream(baos));
    	String message = baos.toString();
    	
      if (StringUtils.isEmpty(message)) {
        message = e.getMessage();
      }
      throw new AssertionError(message);
    }
    return !parser.getParsingState().hasNextToken() || parser.getParsingState().readToken(parser.getParsingState().lexerIndex).getType() == GenericTokenType.EOF;
  }

  public void describeTo(Description desc) {
    desc.appendText("Tokens haven't been all consumed '" + sourceCode + "'");
  }
}
