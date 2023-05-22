/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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

import java.lang.reflect.Field;

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
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#zeroOrMore(Object)} instead.
     */
    @Deprecated
    public static Matcher o2n(Object... e) {
      return new ZeroOrMoreExpression(convertToSingleExpression(e));
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#oneOrMore(Object)} instead.
     */
    @Deprecated
    public static Matcher one2n(Object... e) {
      return new OneOrMoreExpression(convertToSingleExpression(e));
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#optional(Object)} instead.
     */
    @Deprecated
    public static Matcher opt(Object... e) {
      return new OptionalExpression(convertToSingleExpression(e));
    }

    /**
     * @deprecated in 1.16, use {@link GrammarFunctions.Standard#firstOf(Object...)} instead
     */
    @Deprecated
    public static Matcher or(Object... e) {
      return firstOf(e);
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#firstOf(Object, Object)} instead.
     */
    @Deprecated
    public static Matcher firstOf(Object... e) {
      checkSize(e);
      if (e.length == 1) {
        return convertToExpression(e[0]);
      } else {
        return new FirstOfExpression(convertToExpressions(e));
      }
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#sequence(Object, Object)} instead.
     */
    @Deprecated
    public static Matcher and(Object... e) {
      return convertToSingleExpression(e);
    }

  }

  public static final class Predicate {

    private Predicate() {
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#nextNot(Object)} instead.
     */
    @Deprecated
    public static Matcher not(Object e) {
      return new NextNotExpression(convertToExpression(e));
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#next(Object)} instead.
     */
    @Deprecated
    public static Matcher next(Object... e) {
      return new NextExpression(convertToSingleExpression(e));
    }

  }

  public static final class Advanced {

    private Advanced() {
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#adjacent(Object)} instead.
     */
    @Deprecated
    public static Matcher adjacent(Object e) {
      return new SequenceExpression(AdjacentExpression.INSTANCE, convertToExpression(e));
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#anyTokenButNot(Object)} instead.
     */
    @Deprecated
    public static Matcher anyTokenButNot(Object e) {
      return new SequenceExpression(new NextNotExpression(convertToExpression(e)), AnyTokenExpression.INSTANCE);
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#isOneOfThem(TokenType, TokenType...)} instead.
     */
    @Deprecated
    public static Matcher isOneOfThem(TokenType... types) {
      checkSize(types);
      return new TokenTypesExpression(types);
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#bridge(TokenType, TokenType)} instead.
     */
    @Deprecated
    public static Matcher bridge(TokenType from, TokenType to) {
      return new TokensBridgeExpression(from, to);
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#anyToken()} instead.
     */
    @Deprecated
    public static Matcher isTrue() {
      return AnyTokenExpression.INSTANCE;
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#nothing()} instead.
     */
    @Deprecated
    public static Matcher isFalse() {
      return NothingExpression.INSTANCE;
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#anyToken()} instead.
     */
    @Deprecated
    public static Matcher anyToken() {
      return AnyTokenExpression.INSTANCE;
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#tillNewLine()} instead.
     */
    @Deprecated
    public static Matcher tillNewLine() {
      return TillNewLineExpression.INSTANCE;
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#till(Object)} instead.
     */
    @Deprecated
    public static Matcher till(Object e) {
      ParsingExpression expression = convertToExpression(e);
      return new SequenceExpression(
          new ZeroOrMoreExpression(
              new SequenceExpression(
                  new NextNotExpression(expression),
                  AnyTokenExpression.INSTANCE)),
          expression);
    }

    /**
     * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#exclusiveTill(Object)} instead.
     */
    @Deprecated
    public static Matcher exclusiveTill(Object... e) {
      ParsingExpression[] expressions = convertToExpressions(e);
      ParsingExpression subExpression = expressions.length == 1 ? expressions[0] : new FirstOfExpression(expressions);
      return new ZeroOrMoreExpression(
          new SequenceExpression(
              new NextNotExpression(
                  subExpression),
              AnyTokenExpression.INSTANCE));
    }

  }

  /**
   * @since 1.14
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder#buildWithMemoizationOfMatchesForAllRules()} instead.
   */
  @Deprecated
  public static void enableMemoizationOfMatchesForAllRules(Grammar grammar) {
    for (Field ruleField : Grammar.getAllRuleFields(grammar.getClass())) {
      String ruleName = ruleField.getName();
      RuleDefinition rule;
      try {
        rule = (RuleDefinition) ruleField.get(grammar);
      } catch (IllegalAccessException e) {
        throw new IllegalStateException("Unable to enable memoization for rule '" + ruleName + "'", e);
      }
      rule.enableMemoization();
    }
  }

  static ParsingExpression convertToSingleExpression(Object[] e) {
    checkSize(e);
    if (e.length == 1) {
      return convertToExpression(e[0]);
    } else {
      return new SequenceExpression(convertToExpressions(e));
    }
  }

  private static ParsingExpression[] convertToExpressions(Object[] e) {
    checkSize(e);
    ParsingExpression[] matchers = new ParsingExpression[e.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = convertToExpression(e[i]);
    }
    return matchers;
  }

  private static ParsingExpression convertToExpression(Object e) {
    final ParsingExpression expression;
    if (e instanceof String) {
      expression = new TokenValueExpression((String) e);
    } else if (e instanceof TokenType) {
      TokenType tokenType = (TokenType) e;
      expression = new TokenTypeExpression(tokenType);
    } else if (e instanceof RuleDefinition) {
      expression = (RuleDefinition) e;
    } else if (e instanceof Class) {
      expression = new TokenTypeClassExpression((Class) e);
    } else if (e instanceof ParsingExpression) {
      expression = (ParsingExpression) e;
    } else {
      throw new IllegalArgumentException("The matcher object can't be anything else than a Rule, Matcher, String, TokenType or Class. Object = " + e);
    }
    return expression;
  }

  private static void checkSize(Object[] e) {
    if (e == null || e.length == 0) {
      throw new IllegalArgumentException("You must define at least one matcher.");
    }
  }

}
