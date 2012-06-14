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

import static com.google.common.base.Preconditions.*;

public class UnsupportedMatcherException extends RuntimeException {

  private static final long serialVersionUID = -155411423005566354L;
  private final String matcher;

  public UnsupportedMatcherException(Matcher matcher) {
    checkNotNull(matcher, "matcher cannot be null");

    this.matcher = matcher.getClass().getSimpleName();
  }

  @Override
  public String toString() {
    return "The matcher \"" + matcher + "\" is not supported";
  }

}
