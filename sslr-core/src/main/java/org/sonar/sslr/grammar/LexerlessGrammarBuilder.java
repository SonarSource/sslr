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
package org.sonar.sslr.grammar;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.grammar.LexerlessGrammarAdapter;
import org.sonar.sslr.internal.grammar.LexerlessGrammarRuleDefinition;
import org.sonar.sslr.internal.grammar.MatcherBuilderUtils;
import org.sonar.sslr.internal.grammar.ReflexiveMatcherBuilder;
import org.sonar.sslr.internal.matchers.EndOfInputMatcher;
import org.sonar.sslr.internal.matchers.FirstOfMatcher;
import org.sonar.sslr.internal.matchers.NothingMatcher;
import org.sonar.sslr.internal.matchers.OneOrMoreMatcher;
import org.sonar.sslr.internal.matchers.OptionalMatcher;
import org.sonar.sslr.internal.matchers.PatternMatcher;
import org.sonar.sslr.internal.matchers.TestMatcher;
import org.sonar.sslr.internal.matchers.TestNotMatcher;
import org.sonar.sslr.internal.matchers.TokenMatcher;
import org.sonar.sslr.internal.matchers.TriviaMatcher;
import org.sonar.sslr.internal.matchers.ZeroOrMoreMatcher;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A builder for creating grammars for lexerless parsing.
 *
 * @since 1.18
 */
public class LexerlessGrammarBuilder {

  private final Map<GrammarRuleKey, LexerlessGrammarRuleDefinition> definitions = Maps.newHashMap();
  private GrammarRuleKey rootRuleKey;

  public static LexerlessGrammarBuilder create() {
    return new LexerlessGrammarBuilder();
  }

  public static LexerlessGrammarBuilder createBasedOn(LexerlessGrammarBuilder... base) {
    return new LexerlessGrammarBuilder(base);
  }

  private LexerlessGrammarBuilder(LexerlessGrammarBuilder... base) {
    for (LexerlessGrammarBuilder b : base) {
      definitions.putAll(b.definitions);
    }
  }

  /**
   * Allows to describe rule.
   * Result of this method should be used only for execution of methods in it, i.e. you should not save reference on it.
   * No guarantee that this method always returns the same instance for the same key of rule.
   */
  public GrammarRuleBuilder rule(GrammarRuleKey ruleKey) {
    LexerlessGrammarRuleDefinition definition = definitions.get(ruleKey);
    if (definition == null) {
      definition = new LexerlessGrammarRuleDefinition(ruleKey);
      definitions.put(ruleKey, definition);
    }
    return definition;
  }

  /**
   * Allows to specify that given rule should be root for grammar.
   */
  public void setRootRule(GrammarRuleKey ruleKey) {
    rule(ruleKey);
    rootRuleKey = ruleKey;
  }

  /**
   * Constructs grammar.
   *
   * @throws GrammarException if some of rules were used, but not defined
   * @return grammar
   */
  public LexerlessGrammar build() {
    return new LexerlessGrammarAdapter(definitions.values(), rootRuleKey);
  }

  /**
   * Creates expression of grammar - "sequence".
   *
   * @param e1  first sub-expression
   * @param e2  second sub-expression
   */
  public Object sequence(Object e1, Object e2) {
    return MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Arrays.asList(e1, e2));
  }

  /**
   * Creates expression of grammar - "sequence".
   *
   * @param e1  first sub-expression
   * @param e2  second sub-expression
   * @param rest  rest of sub-expressions
   */
  public Object sequence(Object e1, Object e2, Object... rest) {
    return MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, e2, rest));
  }

  /**
   * Creates expression of grammar - "first of".
   *
   * @param e1  first sub-expression
   * @param e2  second sub-expression
   */
  public Object firstOf(Object e1, Object e2) {
    return new ReflexiveMatcherBuilder(FirstOfMatcher.class, MatcherBuilderUtils.lexerlessToMatcherBuilders(Arrays.asList(e1, e2)));
  }

  /**
   * Creates expression of grammar - "first of".
   *
   * @param e1  first sub-expression
   * @param e2  second sub-expression
   * @param rest  rest of sub-expressions
   */
  public Object firstOf(Object e1, Object e2, Object... rest) {
    return new ReflexiveMatcherBuilder(FirstOfMatcher.class, MatcherBuilderUtils.lexerlessToMatcherBuilders(Lists.asList(e1, e2, rest)));
  }

  /**
   * Creates expression of grammar - "optional".
   *
   * @param e  sub-expression
   */
  public Object optional(Object e) {
    return new ReflexiveMatcherBuilder(OptionalMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Arrays.asList(e))});
  }

  /**
   * Creates expression of grammar - "optional".
   *
   * @param e1  first sub-expression
   * @param rest  rest of sub-expressions
   */
  public Object optional(Object e1, Object... rest) {
    return new ReflexiveMatcherBuilder(OptionalMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, rest))});
  }

  /**
   * Creates expression of grammar - "one or more".
   *
   * @param e  sub-expression
   */
  public Object oneOrMore(Object e) {
    return new ReflexiveMatcherBuilder(OneOrMoreMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Arrays.asList(e))});
  }

  /**
   * Creates expression of grammar - "one or more".
   *
   * @param e1  first sub-expression
   * @param rest  rest of sub-expressions
   */
  public Object oneOrMore(Object e1, Object... rest) {
    return new ReflexiveMatcherBuilder(OneOrMoreMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, rest))});
  }

  /**
   * Creates expression of grammar - "zero or more".
   *
   * @param e  sub-expression
   */
  public Object zeroOrMore(Object e) {
    return new ReflexiveMatcherBuilder(ZeroOrMoreMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Arrays.asList(e))});
  }

  /**
   * Creates expression of grammar - "zero or more".
   *
   * @param e1  first sub-expression
   * @param rest  rest of sub-expressions
   */
  public Object zeroOrMore(Object e1, Object... rest) {
    return new ReflexiveMatcherBuilder(ZeroOrMoreMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, rest))});
  }

  /**
   * Creates expression of grammar - "next".
   *
   * @param e  sub-expression
   */
  public Object next(Object e) {
    return new ReflexiveMatcherBuilder(TestMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Arrays.asList(e))});
  }

  /**
   * Creates expression of grammar - "next".
   *
   * @param e1  first sub-expression
   * @param rest  rest of sub-expressions
   */
  public Object next(Object e1, Object... rest) {
    return new ReflexiveMatcherBuilder(TestMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, rest))});
  }

  /**
   * Creates expression of grammar - "next not".
   *
   * @param e  sub-expression
   */
  public Object nextNot(Object e) {
    return new ReflexiveMatcherBuilder(TestNotMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Arrays.asList(e))});
  }

  /**
   * Creates expression of grammar - "next not".
   *
   * @param e1  first sub-expression
   * @param rest  rest of sub-expressions
   */
  public Object nextNot(Object e1, Object... rest) {
    return new ReflexiveMatcherBuilder(TestNotMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, rest))});
  }

  /**
   * Creates expression of grammar - "nothing".
   */
  public Object nothing() {
    return new ReflexiveMatcherBuilder(NothingMatcher.class, new Object[0]);
  }

  /**
   * Creates expression of grammar based on regular expression.
   *
   * @param regexp  regular expression
   * @throws java.util.regex.PatternSyntaxException if the expression's syntax is invalid
   */
  public Object regexp(String regexp) {
    Pattern.compile(regexp);
    return new ReflexiveMatcherBuilder(PatternMatcher.class, new Object[] {regexp});
  }

  /**
   * Creates expression of grammar - "end of input".
   */
  public Object endOfInput() {
    return new ReflexiveMatcherBuilder(EndOfInputMatcher.class, new Object[0]);
  }

  /**
   * Creates expression of grammar - "token".
   *
   * @param e  sub-expression
   */
  public Object token(TokenType tokenType, Object e) {
    return new ReflexiveMatcherBuilder(TokenMatcher.class, new Object[] {tokenType, MatcherBuilderUtils.lexerlessToMatcherBuilder(e)});
  }

  /**
   * Creates expression of grammar - "comment trivia".
   *
   * @param e  sub-expression
   */
  public Object commentTrivia(Object e) {
    return new ReflexiveMatcherBuilder(TriviaMatcher.class, new Object[] {TriviaKind.COMMENT, MatcherBuilderUtils.lexerlessToMatcherBuilder(e)});
  }

  /**
   * Creates expression of grammar - "skipped trivia".
   *
   * @param e  sub-expression
   */
  public Object skippedTrivia(Object e) {
    return new ReflexiveMatcherBuilder(TriviaMatcher.class, new Object[] {TriviaKind.SKIPPED_TEXT, MatcherBuilderUtils.lexerlessToMatcherBuilder(e)});
  }

}
