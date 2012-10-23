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

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
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
    return Objects.hashCode(getClass(), typeClass);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    TokenTypeClassMatcher other = (TokenTypeClassMatcher) obj;
    return Objects.equal(this.typeClass, other.typeClass);
  }

}
