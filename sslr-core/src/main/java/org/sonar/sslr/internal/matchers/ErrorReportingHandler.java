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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

public class ErrorReportingHandler implements MatchHandler {

  private final MatchHandler delegate;
  private final int errorIndex;
  private final List<List<MatcherPathElement>> failedPaths = Lists.newArrayList();

  public ErrorReportingHandler(MatchHandler delegate, int errorIndex) {
    this.delegate = delegate;
    this.errorIndex = errorIndex;
  }

  public boolean match(MatcherContext context) {
    return delegate.match(context);
  }

  public void onMatch(MatcherContext context) {
    delegate.onMatch(context);
  }

  public void onMissmatch(MatcherContext context) {
    delegate.onMissmatch(context);
    // We are interested in errors, which occur only on terminals:
    if (errorIndex == context.getCurrentIndex() && isTerminal(context.getMatcher())) {
      failedPaths.add(getPath((BasicMatcherContext) context));
    }
  }

  private static boolean isTerminal(Matcher matcher) {
    return ((GrammarElementMatcher) matcher).getTokenType() != null;
  }

  private static List<MatcherPathElement> getPath(BasicMatcherContext context) {
    List<MatcherPathElement> list = Lists.newArrayList();
    int endIndex = context.getCurrentIndex();
    while (context != null) {
      if (context.getMatcher() instanceof GrammarElementMatcher) {
        list.add(new MatcherPathElement(context.getMatcher(), context.getStartIndex(), endIndex));
        endIndex = context.getStartIndex();
      }
      context = context.getParent();
    }
    return ImmutableList.copyOf(Iterables.reverse(list));
  }

  public List<List<MatcherPathElement>> getFailedPaths() {
    return failedPaths;
  }

}
