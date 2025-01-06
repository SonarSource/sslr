/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.internal.matchers;

import java.util.Objects;

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
    return Objects.hash(matcher, startIndex, endIndex);
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
