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
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.OrMatcher;

public class EmptyAlternative {

  private final OrMatcher orMatcher;
  private final Matcher alternative;

  public EmptyAlternative(OrMatcher orMatcher, Matcher alternative) {
    this.orMatcher = orMatcher;
    this.alternative = alternative;
  }

  public OrMatcher getOrMatcher() {
    return orMatcher;
  }

  public Matcher getAlternative() {
    return alternative;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (alternative == null ? 0 : alternative.hashCode());
    result = prime * result + (orMatcher == null ? 0 : orMatcher.hashCode());
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
    EmptyAlternative other = (EmptyAlternative) obj;
    if (alternative == null) {
      if (other.alternative != null) {
        return false;
      }
    } else if (!alternative.equals(other.alternative)) {
      return false;
    }
    if (orMatcher == null) {
      if (other.orMatcher != null) {
        return false;
      }
    } else if (!orMatcher.equals(other.orMatcher)) {
      return false;
    }
    return true;
  }

}
