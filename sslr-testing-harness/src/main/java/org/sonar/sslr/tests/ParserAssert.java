/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.tests;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.fest.assertions.GenericAssert;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypeExpression;
import org.sonar.sslr.tests.RuleAssert.EndOfInput;
import org.sonar.sslr.tests.RuleAssert.WithEndOfInput;

/**
 * To create a new instance of this class invoke <code>{@link Assertions#assertThat(Parser)}</code>.
 *
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 *
 * @since 1.16
 */
public class ParserAssert extends GenericAssert<ParserAssert, Parser> {

  public ParserAssert(Parser actual) {
    super(ParserAssert.class, actual);
  }

  private Parser createParserWithEofMatcher() {
    RuleDefinition rule = actual.getRootRule();
    RuleDefinition endOfInput = new RuleDefinition(new EndOfInput())
        .is(new FirstOfExpression(EndOfInputExpression.INSTANCE, new TokenTypeExpression(GenericTokenType.EOF)));
    RuleDefinition withEndOfInput = new RuleDefinition(new WithEndOfInput(actual.getRootRule().getRuleKey()))
        .is(rule, endOfInput);

    Parser parser = Parser.builder(actual).build();
    parser.setRootRule(withEndOfInput);

    return parser;
  }

  /**
   * Verifies that the actual <code>{@link Parser}</code> fully matches a given input.
   * @return this assertion object.
   */
  public ParserAssert matches(String input) {
    isNotNull();
    hasRootRule();
    Parser parser = createParserWithEofMatcher();
    String expected = "Rule '" + getRuleName() + "' should match:\n" + input;
    try {
      parser.parse(input);
    } catch (RecognitionException e) {
      String actual = e.getMessage();
      throw new ParsingResultComparisonFailure(expected, actual);
    }
    return this;
  }

  /**
   * Verifies that the actual <code>{@link Parser}</code> not matches a given input.
   * @return this assertion object.
   */
  public ParserAssert notMatches(String input) {
    isNotNull();
    hasRootRule();
    Parser parser = createParserWithEofMatcher();
    try {
      parser.parse(input);
    } catch (RecognitionException e) {
      // expected
      return this;
    }
    throw new AssertionError("Rule '" + getRuleName() + "' should not match:\n" + input);
  }

  private void hasRootRule() {
    Assertions.assertThat(actual.getRootRule())
        .overridingErrorMessage("Root rule of the parser should not be null")
        .isNotNull();
  }

  private String getRuleName() {
    return actual.getRootRule().getName();
  }

}
