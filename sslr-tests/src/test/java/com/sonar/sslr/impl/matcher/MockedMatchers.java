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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;

import java.util.Arrays;

/**
 * Helpers for tests.
 */
public final class MockedMatchers {

  private MockedMatchers() {
  }

  public static Matcher mockTrue() {
    return new BooleanMatcher(true);
  }

  public static Matcher mockFalse() {
    return new BooleanMatcher(false);
  }

  public static Matcher forInput(boolean... matches) {
    return new InputMatcher(matches);
  }

  private static class BooleanMatcher extends Matcher {
    private final boolean result;

    public BooleanMatcher(boolean result) {
      this.result = result;
    }

    @Override
    public AstNode match(ParsingState parsingState) {
      if (result) {
        parsingState.lexerIndex++;
        return null;
      } else {
        throw BacktrackingEvent.create();
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null || obj.getClass() != getClass()) {
        return false;
      }
      BooleanMatcher other = (BooleanMatcher) obj;
      return this.result == other.result;
    }

    @Override
    public int hashCode() {
      return result ? 1 : 0;
    }
  }

  private static class InputMatcher extends Matcher {
    private final boolean[] matches;

    public InputMatcher(boolean... matches) {
      this.matches = matches;
    }

    @Override
    public AstNode match(ParsingState parsingState) {
      if (parsingState.lexerIndex < matches.length && matches[parsingState.lexerIndex]) {
        parsingState.lexerIndex++;
        return null;
      } else {
        throw BacktrackingEvent.create();
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null || obj.getClass() != getClass()) {
        return false;
      }
      InputMatcher other = (InputMatcher) obj;
      return this.matches == other.matches;
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(matches);
    }
  }

}
