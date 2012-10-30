/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.test.parser;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.sonar.sslr.tests.ParsingResultComparisonFailure;

class ParseMatcher extends BaseMatcher<Parser> {

  private final String sourceCode;

  public ParseMatcher(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public boolean matches(Object obj) {
    if (!(obj instanceof Parser)) {
      return false;
    }
    Parser actual = (Parser) obj;
    if (actual.getRootRule() == null) {
      throw new IllegalStateException("The root rule of the parser is null. No grammar decorator seems to be activated.");
    }

    Parser parser = Parser.builder(actual).setExtendedStackTrace(new ExtendedStackTrace()).build();
    parser.setRootRule(actual.getRootRule());
    String expected = "Rule '" + actual.getRootRule().getRule().getName() + "' should match:\n" + sourceCode;
    try {
      parser.parse(sourceCode);
    } catch (RecognitionException e) {
      throw new ParsingResultComparisonFailure(expected, e.getMessage());
    }
    if (parser.getParsingState().hasNextToken()
        && parser.getParsingState().readToken(parser.getParsingState().lexerIndex).getType() != GenericTokenType.EOF) {
      throw new ParsingResultComparisonFailure(expected, "Not all tokens have been consumed");
    }
    return true;
  }

  public void describeTo(Description desc) {
    desc.appendText("Tokens haven't been all consumed '" + sourceCode + "'");
  }

}
