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

import com.google.common.base.Objects;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public final class TokenTypesMatcher extends TokenMatcher {

  private final Set<TokenType> tokenTypes = new HashSet<TokenType>();

  protected TokenTypesMatcher(TokenType... types) {
    super(false);
    for (TokenType keyword : types) {
      this.tokenTypes.add(keyword);
    }
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return tokenTypes.contains(token.getType());
  }

  @Override
  public String toString() {
    return "isOneOfThem";
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getClass(), tokenTypes);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    TokenTypesMatcher other = (TokenTypesMatcher) obj;
    return Objects.equal(this.tokenTypes, other.tokenTypes);
  }

}
