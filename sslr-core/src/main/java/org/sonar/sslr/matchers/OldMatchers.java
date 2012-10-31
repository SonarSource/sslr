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

public final class OldMatchers {

  private OldMatchers() {
  }

  public static Object and(Object... elements) {
    return Matchers.sequence(elements);
  }

  public static Object or(Object... elements) {
    return Matchers.firstOf(elements);
  }

  public static Object opt(Object... elements) {
    return Matchers.optional(elements);
  }

  public static Object one2n(Object... elements) {
    return Matchers.oneOrMore(elements);
  }

  public static Object o2n(Object... elements) {
    return Matchers.zeroOrMore(elements);
  }

  public static Object next(Object... elements) {
    return Matchers.next(elements);
  }

  public static Object not(Object... elements) {
    return Matchers.nextNot(elements);
  }

}
