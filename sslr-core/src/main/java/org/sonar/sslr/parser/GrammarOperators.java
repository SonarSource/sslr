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
package org.sonar.sslr.parser;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.matchers.MatchersUtils;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.NextExpression;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.NothingExpression;
import org.sonar.sslr.internal.vm.OneOrMoreExpression;
import org.sonar.sslr.internal.vm.OptionalExpression;
import org.sonar.sslr.internal.vm.PatternExpression;
import org.sonar.sslr.internal.vm.TokenExpression;
import org.sonar.sslr.internal.vm.TriviaExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;

/**
 * Provides methods to define rules for {@link LexerlessGrammar}.
 *
 * @since 1.16
 * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder} instead.
 */
@Deprecated
public final class GrammarOperators {

  private GrammarOperators() {
  }

  public static Object sequence(Object... elements) {
    return MatchersUtils.convertToSingleMatcher(elements);
  }

  public static Object firstOf(Object... elements) {
    Preconditions.checkNotNull(elements);

    if (elements.length == 1) {
      return MatchersUtils.convertToMatcher(elements[0]);
    }
    return new FirstOfExpression(MatchersUtils.convertToMatchers(elements));
  }

  public static Object optional(Object... elements) {
    return new OptionalExpression(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object oneOrMore(Object... elements) {
    return new OneOrMoreExpression(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object zeroOrMore(Object... elements) {
    return new ZeroOrMoreExpression(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object next(Object... elements) {
    return new NextExpression(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object nextNot(Object... elements) {
    return new NextNotExpression(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object regexp(String regexp) {
    return new PatternExpression(regexp);
  }

  public static Object endOfInput() {
    return EndOfInputExpression.INSTANCE;
  }

  public static Object nothing() {
    return NothingExpression.INSTANCE;
  }

  public static Object token(TokenType tokenType, Object element) {
    return new TokenExpression(tokenType, MatchersUtils.convertToMatcher(element));
  }

  /**
   * @since 1.17
   */
  public static Object commentTrivia(Object element) {
    return new TriviaExpression(TriviaKind.COMMENT, MatchersUtils.convertToMatcher(element));
  }

  /**
   * @since 1.17
   */
  public static Object skippedTrivia(Object element) {
    return new TriviaExpression(TriviaKind.SKIPPED_TEXT, MatchersUtils.convertToMatcher(element));
  }

}
