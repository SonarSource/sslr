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
    if (errorIndex == context.getCurrentIndex() && !context.isIgnoreErrors()) {
      List<MatcherPathElement> path = getPath((BasicMatcherContext) context);
      if (isNewPath(path)) {
        failedPaths.add(path);
      }
    }
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

  private boolean isNewPath(List<MatcherPathElement> path) {
    for (List<MatcherPathElement> old : Iterables.reverse(failedPaths)) {
      if (isPrefix(path, old)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isPrefix(List<MatcherPathElement> first, List<MatcherPathElement> second) {
    if (first.size() > second.size()) {
      return false;
    }
    for (int i = 0; i < first.size(); i++) {
      MatcherPathElement e1 = first.get(i);
      MatcherPathElement e2 = second.get(i);
      if (!(e1.getMatcher().equals(e2.getMatcher())
          && e1.getStartIndex() == e2.getStartIndex()
          && e1.getEndIndex() == e2.getEndIndex())) {
        return false;
      }
    }
    return true;
  }

  public List<List<MatcherPathElement>> getFailedPaths() {
    return failedPaths;
  }

}
