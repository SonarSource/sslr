/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.sonar.sslr.parser;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.NextExpression;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.NothingExpression;
import org.sonar.sslr.internal.vm.OneOrMoreExpression;
import org.sonar.sslr.internal.vm.OptionalExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.PatternExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.StringExpression;
import org.sonar.sslr.internal.vm.TokenExpression;
import org.sonar.sslr.internal.vm.TriviaExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;

/**
 * @since 1.16
 * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder} instead.
 */
@Deprecated
public final class GrammarOperators {

  private GrammarOperators() {
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#sequence(Object, Object)} instead.
   */
  @Deprecated
  public static Object sequence(Object... e) {
    return convertToSingleExpression(e);
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#firstOf(Object, Object)} instead.
   */
  @Deprecated
  public static Object firstOf(Object... e) {
    Preconditions.checkNotNull(e);

    if (e.length == 1) {
      return convertToExpression(e[0]);
    }
    return new FirstOfExpression(convertToExpressions(e));
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#optional(Object)} instead.
   */
  @Deprecated
  public static Object optional(Object... e) {
    return new OptionalExpression(convertToSingleExpression(e));
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#oneOrMore(Object)} instead.
   */
  @Deprecated
  public static Object oneOrMore(Object... e) {
    return new OneOrMoreExpression(convertToSingleExpression(e));
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#zeroOrMore(Object)} instead.
   */
  @Deprecated
  public static Object zeroOrMore(Object... e) {
    return new ZeroOrMoreExpression(convertToSingleExpression(e));
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#next(Object)} instead.
   */
  @Deprecated
  public static Object next(Object... e) {
    return new NextExpression(convertToSingleExpression(e));
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#nextNot(Object)} instead.
   */
  @Deprecated
  public static Object nextNot(Object... e) {
    return new NextNotExpression(convertToSingleExpression(e));
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#regexp(String)} instead.
   */
  @Deprecated
  public static Object regexp(String regexp) {
    return new PatternExpression(regexp);
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#endOfInput()} instead.
   */
  @Deprecated
  public static Object endOfInput() {
    return EndOfInputExpression.INSTANCE;
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#nothing()} instead.
   */
  @Deprecated
  public static Object nothing() {
    return NothingExpression.INSTANCE;
  }

  /**
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#token(TokenType, Object)} instead.
   */
  @Deprecated
  public static Object token(TokenType tokenType, Object e) {
    return new TokenExpression(tokenType, convertToExpression(e));
  }

  /**
   * @since 1.17
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#commentTrivia(Object)} instead.
   */
  @Deprecated
  public static Object commentTrivia(Object e) {
    return new TriviaExpression(TriviaKind.COMMENT, convertToExpression(e));
  }

  /**
   * @since 1.17
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder#skippedTrivia(Object)} instead.
   */
  @Deprecated
  public static Object skippedTrivia(Object e) {
    return new TriviaExpression(TriviaKind.SKIPPED_TEXT, convertToExpression(e));
  }

  private static ParsingExpression convertToSingleExpression(Object... elements) {
    Preconditions.checkNotNull(elements);

    if (elements.length == 1) {
      return convertToExpression(elements[0]);
    }
    return new SequenceExpression(convertToExpressions(elements));
  }

  private static ParsingExpression[] convertToExpressions(Object... elements) {
    Preconditions.checkNotNull(elements);
    Preconditions.checkArgument(elements.length > 0);

    ParsingExpression[] matchers = new ParsingExpression[elements.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = convertToExpression(elements[i]);
    }
    return matchers;
  }

  private static ParsingExpression convertToExpression(Object e) {
    Preconditions.checkNotNull(e);

    if (e instanceof ParsingExpression) {
      return (ParsingExpression) e;
    } else if (e instanceof String) {
      return new StringExpression((String) e);
    } else if (e instanceof Character) {
      return new StringExpression(((Character) e).toString());
    } else {
      throw new IllegalArgumentException("Incorrect type of parsing expression: " + e.getClass().toString());
    }
  }

}
