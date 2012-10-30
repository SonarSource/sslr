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
package org.sonar.sslr.tests;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import org.fest.assertions.GenericAssert;

/**
 * To create a new instance of this class invoke <code>{@link Assertions#assertThat(Parser)}</code>.
 *
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public class ParserAssert extends GenericAssert<ParserAssert, Parser> {

  public ParserAssert(Parser actual) {
    super(ParserAssert.class, actual);
    isNotNull();
  }

  /**
   * Verifies that the actual <code>{@link Parser}</code> fully matches a given input.
   * @return this assertion object.
   */
  public ParserAssert matches(String input) {
    hasRootRule();
    Parser parser = Parser.builder(actual).setExtendedStackTrace(new ExtendedStackTrace()).build();
    parser.setRootRule(actual.getRootRule());
    String expected = "Rule '" + getRuleName() + "' should match:\n" + input;
    try {
      parser.parse(input);
    } catch (RecognitionException e) {
      String actual = e.getMessage();
      throw new ParsingResultComparisonFailure(expected, actual);
    }
    if (!isAllTokensConsumed(parser)) {
      String actual = "Not all tokens have been consumed";
      throw new ParsingResultComparisonFailure(expected, actual);
    }
    return this;
  }

  /**
   * Verifies that the actual <code>{@link Parser}</code> not matches a given input.
   * @return this assertion object.
   */
  public ParserAssert notMatches(String input) {
    hasRootRule();
    Parser parser = actual;
    try {
      parser.parse(input);
    } catch (RecognitionException e) {
      // expected
      return this;
    }
    if (isAllTokensConsumed(parser)) {
      throw new AssertionError("Rule '" + getRuleName() + "' should not match:\n" + input);
    }
    return this;
  }

  private void hasRootRule() {
    Assertions.assertThat(actual.getRootRule())
        .overridingErrorMessage("Root rule of the parser should not be null")
        .isNotNull();
  }

  private static boolean isAllTokensConsumed(Parser parser) {
    return !parser.getParsingState().hasNextToken()
        || parser.getParsingState().readToken(parser.getParsingState().lexerIndex).getType() == GenericTokenType.EOF;
  }

  private String getRuleName() {
    return actual.getRootRule().getRule().getName();
  }

}
