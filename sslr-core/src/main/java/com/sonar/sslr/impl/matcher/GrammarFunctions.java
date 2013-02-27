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
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.TokenType;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.NextExpression;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.NothingExpression;
import org.sonar.sslr.internal.vm.OneOrMoreExpression;
import org.sonar.sslr.internal.vm.OptionalExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;
import org.sonar.sslr.internal.vm.lexerful.AdjacentExpression;
import org.sonar.sslr.internal.vm.lexerful.AnyTokenExpression;
import org.sonar.sslr.internal.vm.lexerful.TillNewLineExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypeClassExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypeExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypesExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenValueExpression;
import org.sonar.sslr.internal.vm.lexerful.TokensBridgeExpression;

/**
 * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder} instead.
 */
@Deprecated
public final class GrammarFunctions {

  private GrammarFunctions() {
  }

  public static final class Standard {

    private Standard() {
    }

    /**
     * Match elements sequence zero or more times
     *
     * <pre>
     * {@code
     *     --------<----------------<------------------<--------
     *    |                                                     |
     * >------element 1----element 2---- ... ----element n---------->
     *    |                                                     |
     *     ------------------------->---------------------------
     * }
     * </pre>
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#zeroOrMore(Object)} instead.
     */
    @Deprecated
    public static Matcher o2n(Object... objects) {
      return new ZeroOrMoreExpression((ParsingExpression) and(objects));
    }

    /**
     * Match elements sequence one or more times
     *
     * <pre>
     * {@code
     *     ------<----------------<------------------<------
     *    |                                                 |
     * >------element 1----element 2---- ... ----element n------>
     * }
     * </pre>
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#oneOrMore(Object)} instead.
     */
    @Deprecated
    public static Matcher one2n(Object... elements) {
      return new OneOrMoreExpression((ParsingExpression) and(elements));
    }

    /**
     * Optionally match the element(s).
     *
     * <pre>
     * {@code
     * >------element 1----element 2----element n---->
     *    |                                       |
     *     ---------------------------------------
     * }
     * </pre>
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#optional(Object)} instead.
     */
    @Deprecated
    public static Matcher opt(Object... elements) {
      return new OptionalExpression((ParsingExpression) and(elements));
    }

    /**
     * Match any alternative within the elements exactly once
     *
     * <pre>
     * {@code
     * >------element 1----->
     *    |               |
     *    ----element 2---
     *    |               |
     *    ----   ...   ---
     *    |               |
     *    ----element n---
     * }
     * </pre>
     *
     * @deprecated in 1.16, use {@link GrammarFunctions.Standard#firstOf(Object...)} instead
     */
    @Deprecated
    public static Matcher or(Object... elements) {
      return firstOf(elements);
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#firstOf(Object, Object)} instead.
     */
    @Deprecated
    public static Matcher firstOf(Object... elements) {
      checkSize(elements);
      if (elements.length == 1) {
        return convertToMatcher(elements[0]);
      } else {
        return new FirstOfExpression(convertToMatchers(elements));
      }
    }

    /**
     * Match all elements in a sequential order.
     *
     * <pre>
     * {@code
     * >------element 1----element 2---- ... ----element n---->
     * }
     * </pre>
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#sequence(Object, Object)} instead.
     */
    @Deprecated
    public static Matcher and(Object... elements) {
      checkSize(elements);
      if (elements.length == 1) {
        return convertToMatcher(elements[0]);
      } else {
        return new SequenceExpression(convertToMatchers(elements));
      }
    }

  }

  public static final class Predicate {

    private Predicate() {
    }

    /**
     * Syntactic predicate to check that the next tokens don't match an element.
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#nextNot(Object)} instead.
     */
    @Deprecated
    public static Matcher not(Object element) {
      return new NextNotExpression((ParsingExpression) convertToMatcher(element));
    }

    /**
     * Syntactic predicate to check that the next tokens match some elements.
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#next(Object)} instead.
     */
    @Deprecated
    public static Matcher next(Object... elements) {
      return new NextExpression((ParsingExpression) Standard.and(elements));
    }

  }

  public static final class Advanced {

    private Advanced() {
    }

    /**
     * Match the element if and only if the first token of this element is adjacent to the previous consumed token.
     *
     * <pre>
     * {@code
     * >------previous_element---- element ---->
     * }
     * </pre>
     *
     * Without any space between previous_element and element
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#adjacent(Object)} instead.
     */
    @Deprecated
    public static Matcher adjacent(Object element) {
      return new SequenceExpression(AdjacentExpression.INSTANCE, (ParsingExpression) convertToMatcher(element));
    }

    /**
     * Consume the next token if and only if the element doesn't match
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#anyTokenButNot(Object)} instead.
     */
    @Deprecated
    public static Matcher anyTokenButNot(Object element) {
      return new SequenceExpression(new NextNotExpression((ParsingExpression) convertToMatcher(element)), AnyTokenExpression.INSTANCE);
    }

    /**
     * Match the next token if and only if its type belongs to the provided list
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#isOneOfThem(TokenType, TokenType...)} instead.
     */
    @Deprecated
    public static Matcher isOneOfThem(TokenType... types) {
      checkSize(types);
      return new TokenTypesExpression(types);
    }

    /**
     * Consume all tokens between token from and token to.
     *
     * <pre>
     * {@code
     * >------ from ---- ... ---- to ---->
     * }
     * </pre>
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#bridge(TokenType, TokenType)} instead.
     */
    @Deprecated
    public static Matcher bridge(TokenType from, TokenType to) {
      return new TokensBridgeExpression(from, to);
    }

    /**
     * For unit test only Consume the next token whatever it is
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#anyToken()} instead.
     */
    @Deprecated
    public static Matcher isTrue() {
      return anyToken();
    }

    /**
     * For unit test only Not consume the next token whatever it is
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#nothing()} instead.
     */
    @Deprecated
    public static Matcher isFalse() {
      return NothingExpression.INSTANCE;
    }

    /**
     * Consume the next token whatever it is
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#anyToken()} instead.
     */
    @Deprecated
    public static Matcher anyToken() {
      return AnyTokenExpression.INSTANCE;
    }

    /**
     * Consume every following token which are on the current line
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#tillNewLine()} instead.
     */
    @Deprecated
    public static Matcher tillNewLine() {
      return TillNewLineExpression.INSTANCE;
    }

    /**
     * Consume all tokens as long as the element is not encountered. The element is also consumed.
     *
     * <pre>
     * {@code
     * >------ ... ---- element ---->
     * }
     * </pre>
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#till(Object)} instead.
     */
    @Deprecated
    public static Matcher till(Object element) {
      ParsingExpression expression = (ParsingExpression) convertToMatcher(element);
      return new SequenceExpression(
          new ZeroOrMoreExpression(
              new SequenceExpression(
                  new NextNotExpression(expression),
                  AnyTokenExpression.INSTANCE)),
          expression);
    }

    /**
     * Consume all tokens as long one of the provided elements is not encountered.
     *
     * <pre>
     * {@code
     * >------ ... ---- element 1 ---->
     *              |-- element 2 --|
     *              |--    ...    --|
     *              |-- element n --|
     * }
     * </pre>
     *
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#exclusiveTill(Object, Object...)} instead.
     */
    @Deprecated
    public static Matcher exclusiveTill(Object... elements) {
      return new ZeroOrMoreExpression(
          new SequenceExpression(
              new NextNotExpression(
                  // TODO firstOf is useless in case of single sub-expression
                  new FirstOfExpression(convertToMatchers(elements))),
              AnyTokenExpression.INSTANCE));
    }

    /**
     * @since 1.14
     */
    public static Matcher memoizeMatches(Object element) {
      return convertToMatcher(element);
    }

  }

  /**
   * Allows to enable memoization for all rules in given grammar.
   * <p>
   * Usage of {@link GrammarFunctions.Advanced#memoizeMatches(Object)} is preferable than this method, because provides fine-grained control over memoization.
   * Also note that this method was introduced in order to simplify migration from version 1.13 and may disappear in future versions.
   * </p>
   *
   * @since 1.14
   */
  public static void enableMemoizationOfMatchesForAllRules(Grammar grammar) {
    // TODO
  }

  protected static ParsingExpression[] convertToMatchers(Object[] objects) {
    checkSize(objects);
    ParsingExpression[] matchers = new ParsingExpression[objects.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = (ParsingExpression) convertToMatcher(objects[i]);
    }
    return matchers;
  }

  protected static Matcher convertToMatcher(Object object) {
    final Matcher matcher;
    if (object instanceof String) {
      matcher = new TokenValueExpression((String) object);
    } else if (object instanceof TokenType) {
      TokenType tokenType = (TokenType) object;
      matcher = new TokenTypeExpression(tokenType);
    } else if (object instanceof RuleDefinition) {
      matcher = ((RuleDefinition) object);
    } else if (object instanceof Class) {
      matcher = new TokenTypeClassExpression((Class) object);
    } else if (object instanceof Matcher) {
      matcher = (Matcher) object;
    } else {
      throw new IllegalArgumentException("The matcher object can't be anything else than a Rule, Matcher, String, TokenType or Class. Object = " + object);
    }
    return matcher;
  }

  private static void checkSize(Object[] objects) {
    if (objects == null || objects.length == 0) {
      throw new IllegalArgumentException("You must define at least one matcher.");
    }
  }

}
