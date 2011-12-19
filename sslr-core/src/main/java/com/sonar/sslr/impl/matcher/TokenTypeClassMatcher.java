/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.Token;

public final class TokenTypeClassMatcher extends TokenMatcher {

  private final Class typeClass;

  protected TokenTypeClassMatcher(Class typeClass) {
    super(false);
    this.typeClass = typeClass;
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return typeClass == token.getType().getClass();
  }

  @Override
  public String toString() {
    return typeClass.getCanonicalName() + ".class";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getClass().hashCode();
    result = prime * result + (typeClass == null ? 0 : typeClass.hashCode());
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
    TokenTypeClassMatcher other = (TokenTypeClassMatcher) obj;
    if (typeClass == null) {
      if (other.typeClass != null) {
        return false;
      }
    } else if ( !typeClass.equals(other.typeClass)) {
      return false;
    }
    return true;
  }

}
