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

import com.sonar.sslr.impl.ParsingState;

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 *
 * @since 1.14
 */
public abstract class DelegatingMatcher extends StandardMatcher {

  public DelegatingMatcher(Matcher delegate) {
    super(delegate);
  }

  protected Matcher getDelegate() {
    return this.children[0];
  }

  @Override
  protected MatchResult doMatch(ParsingState parsingState) {
    return getDelegate().doMatch(parsingState);
  }

  @Override
  public String toString() {
    return getDelegate().toString();
  }

  public static Matcher unwrap(Matcher matcher) {
    return matcher instanceof DelegatingMatcher ? matcher.children[0] : matcher;
  }

}
