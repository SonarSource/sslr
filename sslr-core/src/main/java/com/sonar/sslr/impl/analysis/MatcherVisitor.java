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

import com.sonar.sslr.impl.matcher.*;

public abstract class MatcherVisitor<R, U> {

  public MatcherVisitor() {
  }

  public final R visit(Matcher matcher) {
    return visit(matcher, null);
  }

  public final R visit(Matcher matcher, U userObject) {
    final R result;
    if (matcher instanceof TokenMatcher) {
      result = visit((TokenMatcher) matcher, userObject);
    } else if (matcher instanceof OrMatcher) {
      result = visit((OrMatcher) matcher, userObject);
    } else if (matcher instanceof AndMatcher) {
      result = visit((AndMatcher) matcher, userObject);
    } else if (matcher instanceof OptMatcher) {
      result = visit((OptMatcher) matcher, userObject);
    } else if (matcher instanceof OneToNMatcher) {
      result = visit((OneToNMatcher) matcher, userObject);
    } else if (matcher instanceof RuleMatcher) {
      result = visit((RuleMatcher) matcher, userObject);
    } else {
      throw new UnsupportedMatcherException(matcher);
    }
    return result;
  }

  public abstract R visit(TokenMatcher matcher, U userObject);

  public abstract R visit(OrMatcher matcher, U userObject);

  public abstract R visit(AndMatcher matcher, U userObject);

  public abstract R visit(OptMatcher matcher, U userObject);

  public abstract R visit(OneToNMatcher matcher, U userObject);

  public abstract R visit(RuleMatcher matcher, U userObject);

}
