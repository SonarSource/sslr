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

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public final class TokenTypeMatcher extends TokenMatcher {

  private final TokenType type;

  protected TokenTypeMatcher(TokenType type) {
    this(type, false);
  }

  protected TokenTypeMatcher(TokenType type, boolean hasToBeSkippedFromAst) {
    super(hasToBeSkippedFromAst);
    this.type = type;
  }

  public TokenType getType() {
    return type;
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return type == token.getType();
  }

  @Override
  public String toString() {
    return type.getName();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getClass().hashCode();
    result = prime * result + (type == null ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TokenTypeMatcher other = (TokenTypeMatcher) obj;
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if ( !type.equals(other.type)) {
      return false;
    }
    return true;
  }

}
