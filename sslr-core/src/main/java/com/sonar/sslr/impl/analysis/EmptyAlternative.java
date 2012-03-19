/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
