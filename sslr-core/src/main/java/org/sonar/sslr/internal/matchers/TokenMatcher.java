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
package org.sonar.sslr.internal.matchers;

import com.sonar.sslr.api.TokenType;

public class TokenMatcher implements Matcher {

  private final TokenType tokenType;
  private final Matcher subMatcher;

  public TokenMatcher(TokenType tokenType, Matcher subMatcher) {
    this.tokenType = tokenType;
    this.subMatcher = subMatcher;
  }

  public boolean match(MatcherContext context) {
    context.ignoreErrors();
    if (context.getSubContext(subMatcher).runMatcher()) {
      context.createNode();
      return true;
    }
    return false;
  }

  public TokenType getTokenType() {
    return tokenType;
  }

}
