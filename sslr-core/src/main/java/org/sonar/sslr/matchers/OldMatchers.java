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
package org.sonar.sslr.matchers;

/**
 * Provides methods to simplify migration from {@link com.sonar.sslr.impl.matcher.GrammarFunctions}
 * to {@link Matchers}.
 *
 * @since 2.0
 */
public final class OldMatchers {

  private OldMatchers() {
  }

  /**
   * @deprecated use {@link Matchers#sequence(Object...)} instead
   */
  @Deprecated
  public static Object and(Object... elements) {
    return Matchers.sequence(elements);
  }

  /**
   * @deprecated use {@link Matchers#firstOf(Object...)} instead
   */
  @Deprecated
  public static Object or(Object... elements) {
    return Matchers.firstOf(elements);
  }

  /**
   * @deprecated use {@link Matchers#optional(Object...)} instead
   */
  @Deprecated
  public static Object opt(Object... elements) {
    return Matchers.optional(elements);
  }

  /**
   * @deprecated use {@link Matchers#oneOrMore(Object...)} instead
   */
  @Deprecated
  public static Object one2n(Object... elements) {
    return Matchers.oneOrMore(elements);
  }

  /**
   * @deprecated use {@link Matchers#zeroOrMore(Object...)} instead
   */
  @Deprecated
  public static Object o2n(Object... elements) {
    return Matchers.zeroOrMore(elements);
  }

  /**
   * @deprecated use {@link Matchers#next(Object...)} instead
   */
  @Deprecated
  public static Object next(Object... elements) {
    return Matchers.next(elements);
  }

  /**
   * @deprecated use {@link Matchers#nextNot(Object...)} instead
   */
  @Deprecated
  public static Object not(Object... elements) {
    return Matchers.nextNot(elements);
  }

  /**
   * @deprecated use {@link Matchers#nothing()} instead
   */
  @Deprecated
  public static Object isFalse() {
    return Matchers.nothing();
  }

}
