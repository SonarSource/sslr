/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.HashSet;
import java.util.Set;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

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
    final int prime = 31;
    int result = 1;
    result = prime * result + getClass().hashCode();
    result = prime * result + (tokenTypes == null ? 0 : tokenTypes.hashCode());
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
    TokenTypesMatcher other = (TokenTypesMatcher) obj;
    if (tokenTypes == null) {
      if (other.tokenTypes != null) {
        return false;
      }
    } else if ( !tokenTypes.equals(other.tokenTypes)) {
      return false;
    }
    return true;
  }

}
