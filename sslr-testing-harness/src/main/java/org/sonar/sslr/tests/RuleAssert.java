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

import com.sonar.sslr.api.Rule;
import org.fest.assertions.GenericAssert;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.parser.ParseErrorFormatter;
import org.sonar.sslr.parser.ParseRunner;
import org.sonar.sslr.parser.ParsingResult;

/**
 * To create a new instance of this class invoke <code>{@link Assertions#assertThat(Rule)}</code>.
 *
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 *
 * @since 1.16
 */
public class RuleAssert extends GenericAssert<RuleAssert, Rule> {

  public RuleAssert(Rule actual) {
    super(RuleAssert.class, actual);
  }

  private static class WithEndOfInput implements GrammarRuleKey {
    private final GrammarRuleKey ruleKey;

    public WithEndOfInput(GrammarRuleKey ruleKey) {
      this.ruleKey = ruleKey;
    }

    @Override
    public String toString() {
      return ruleKey + " with end of input";
    }
  }

  private static class EndOfInput implements GrammarRuleKey {
    @Override
    public String toString() {
      return "end of input";
    }
  }

  private ParseRunner createParseRunnerWithEofMatcher() {
    isNotNull();

    MutableParsingRule rule = (MutableParsingRule) actual;
    MutableParsingRule endOfInput = (MutableParsingRule) new MutableParsingRule(new EndOfInput())
        .is(EndOfInputExpression.INSTANCE);
    MutableParsingRule withEndOfInput = (MutableParsingRule) new MutableParsingRule(new WithEndOfInput(rule.getRuleKey()))
        .is(actual, endOfInput);
    return new ParseRunner(withEndOfInput);
  }

  /**
   * Verifies that the actual <code>{@link Rule}</code> fully matches a given input.
   * @return this assertion object.
   */
  public RuleAssert matches(String input) {
    ParseRunner parseRunner = createParseRunnerWithEofMatcher();
    ParsingResult parsingResult = parseRunner.parse(input.toCharArray());
    if (!parsingResult.isMatched()) {
      String expected = "Rule '" + getRuleName() + "' should match:\n" + input;
      String actual = new ParseErrorFormatter().format(parsingResult.getParseError());
      throw new ParsingResultComparisonFailure(expected, actual);
    }
    return this;
  }

  /**
   * Verifies that the actual <code>{@link Rule}</code> does not match a given input.
   * @return this assertion object.
   */
  public RuleAssert notMatches(String input) {
    ParseRunner parseRunner = createParseRunnerWithEofMatcher();
    ParsingResult parsingResult = parseRunner.parse(input.toCharArray());
    if (parsingResult.isMatched()) {
      throw new AssertionError("Rule '" + getRuleName() + "' should not match:\n" + input);
    }
    return this;
  }

  private ParseRunner createParseRunnerWithoutEofMatcher() {
    isNotNull();
    return new ParseRunner(actual);
  }

  /**
   * Verifies that the actual <code>{@link Rule}</code> partially matches a given input.
   * @param prefixToBeMatched the prefix that must be fully matched
   * @param remainingInput the remainder of the input, which is not to be matched
   * @return this assertion object.
   */
  public RuleAssert matchesPrefix(String prefixToBeMatched, String remainingInput) {
    ParseRunner parseRunner = createParseRunnerWithoutEofMatcher();
    String input = prefixToBeMatched + remainingInput;
    ParsingResult parsingResult = parseRunner.parse(input.toCharArray());
    if (!parsingResult.isMatched()) {
      String expected = "Rule '" + getRuleName() + "' should match:\n" + prefixToBeMatched + "\nwhen followed by:\n" + remainingInput;
      String actual = new ParseErrorFormatter().format(parsingResult.getParseError());
      throw new ParsingResultComparisonFailure(expected, actual);
    } else if (prefixToBeMatched.length() != parsingResult.getParseTreeRoot().getEndIndex()) {
      String actualMatchedPrefix = input.substring(0, parsingResult.getParseTreeRoot().getEndIndex());
      String message = "Rule '" + getRuleName() + "' should match:\n" + prefixToBeMatched + "\nwhen followed by:\n" + remainingInput + "\nbut matched:\n" + actualMatchedPrefix;
      throw new ParsingResultComparisonFailure(message, prefixToBeMatched, actualMatchedPrefix);
    }

    return this;
  }

  private String getRuleName() {
    return ((MutableParsingRule) actual).getName();
  }

}
