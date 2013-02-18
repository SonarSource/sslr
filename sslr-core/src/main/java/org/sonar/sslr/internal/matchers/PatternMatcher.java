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

import org.sonar.sslr.grammar.GrammarException;

import java.util.regex.Pattern;

public class PatternMatcher implements Matcher {

  private final java.util.regex.Matcher matcher;

  /**
   * @throws java.util.regex.PatternSyntaxException if the expression's syntax is invalid
   */
  public PatternMatcher(String regex) {
    matcher = Pattern.compile(regex).matcher("");
  }

  /**
   * @throws GrammarException if execution of regular expression has led to StackOverflowError
   */
  public boolean match(MatcherContext context) {
    matcher.reset(context);
    boolean result;
    try {
      result = matcher.lookingAt();
    } catch (StackOverflowError e) {
      throw new GrammarException(e, "The regular expression '" + matcher.pattern().pattern() + "' has led to a stack overflow error."
          + " This error is certainly due to an inefficient use of alternations. See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5050507");
    }
    if (result && matcher.end() != 0) {
      context.advanceIndex(matcher.end());
      context.createNode();
    }
    return result;
  }

}
