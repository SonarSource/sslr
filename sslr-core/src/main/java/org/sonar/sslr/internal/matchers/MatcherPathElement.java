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
package org.sonar.sslr.internal.matchers;

import com.google.common.base.Objects;

public class MatcherPathElement {

  private final Matcher matcher;
  private final int startIndex;
  private final int endIndex;

  public MatcherPathElement(Matcher matcher, int startIndex, int endIndex) {
    this.matcher = matcher;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
  }

  public Matcher getMatcher() {
    return matcher;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public int getEndIndex() {
    return endIndex;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(matcher, startIndex, endIndex);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof MatcherPathElement) {
      MatcherPathElement other = (MatcherPathElement) obj;
      return this.matcher.equals(other.matcher)
        && this.startIndex == other.startIndex
        && this.endIndex == other.endIndex;
    }
    return false;
  }

}
