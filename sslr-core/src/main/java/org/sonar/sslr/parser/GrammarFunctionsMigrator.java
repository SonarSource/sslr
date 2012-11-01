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

/**
 * Provides methods to simplify migration from {@link com.sonar.sslr.impl.matcher.GrammarFunctions}
 * to {@link GrammarOperators}.
 *
 * @since 2.0
 */
public final class GrammarFunctionsMigrator {

  private GrammarFunctionsMigrator() {
  }

  /**
   * @deprecated use {@link GrammarOperators#sequence(Object...)} instead
   */
  @Deprecated
  public static Object and(Object... elements) {
    return GrammarOperators.sequence(elements);
  }

  /**
   * @deprecated use {@link GrammarOperators#firstOf(Object...)} instead
   */
  @Deprecated
  public static Object or(Object... elements) {
    return GrammarOperators.firstOf(elements);
  }

  /**
   * @deprecated use {@link GrammarOperators#optional(Object...)} instead
   */
  @Deprecated
  public static Object opt(Object... elements) {
    return GrammarOperators.optional(elements);
  }

  /**
   * @deprecated use {@link GrammarOperators#oneOrMore(Object...)} instead
   */
  @Deprecated
  public static Object one2n(Object... elements) {
    return GrammarOperators.oneOrMore(elements);
  }

  /**
   * @deprecated use {@link GrammarOperators#zeroOrMore(Object...)} instead
   */
  @Deprecated
  public static Object o2n(Object... elements) {
    return GrammarOperators.zeroOrMore(elements);
  }

  /**
   * @deprecated use {@link GrammarOperators#next(Object...)} instead
   */
  @Deprecated
  public static Object next(Object... elements) {
    return GrammarOperators.next(elements);
  }

  /**
   * @deprecated use {@link GrammarOperators#nextNot(Object...)} instead
   */
  @Deprecated
  public static Object not(Object... elements) {
    return GrammarOperators.nextNot(elements);
  }

  /**
   * @deprecated use {@link GrammarOperators#nothing()} instead
   */
  @Deprecated
  public static Object isFalse() {
    return GrammarOperators.nothing();
  }

}
