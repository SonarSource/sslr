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

public class ErrorLocatingHandler implements MatchHandler {

  private final MatchHandler delegate;
  private int errorIndex = -1;

  public ErrorLocatingHandler(MatchHandler delegate) {
    this.delegate = delegate;
  }

  public boolean match(MatcherContext context) {
    return delegate.match(context);
  }

  public void onMatch(MatcherContext context) {
    delegate.onMatch(context);
  }

  public void onMissmatch(MatcherContext context) {
    delegate.onMissmatch(context);
    // FIXME Godin: for the moment we assume that error cannot occur inside of predicate or inside of terminal
    if (errorIndex < context.getCurrentIndex()) {
      errorIndex = context.getCurrentIndex();
    }
  }

  public int getErrorIndex() {
    return errorIndex;
  }

}
