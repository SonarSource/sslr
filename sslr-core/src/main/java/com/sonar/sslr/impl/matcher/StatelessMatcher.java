/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import java.util.Arrays;

public abstract class StatelessMatcher extends MemoizedMatcher {

  protected StatelessMatcher(Matcher... children) {
    super(children);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getClass().hashCode();
    result = prime * result + Arrays.hashCode(children);
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
    StatelessMatcher other = (StatelessMatcher) obj;
    if ( !Arrays.equals(children, other.children)) {
      return false;
    }
    return true;
  }

}
