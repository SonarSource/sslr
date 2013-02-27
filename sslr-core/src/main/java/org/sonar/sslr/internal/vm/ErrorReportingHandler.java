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
package org.sonar.sslr.internal.vm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.matchers.MatcherPathElement;

import java.util.List;

public class ErrorReportingHandler implements MachineHandler {

  private final int errorIndex;
  private final List<List<MatcherPathElement>> failedPaths = Lists.newArrayList();

  public ErrorReportingHandler(int errorIndex) {
    this.errorIndex = errorIndex;
  }

  public List<List<MatcherPathElement>> getFailedPaths() {
    return failedPaths;
  }

  public void onBacktrack(Machine machine) {
    if (errorIndex == machine.getIndex()) {
      List<MatcherPathElement> path = getPath(machine);
      if (isNewPath(path)) {
        failedPaths.add(path);
      }
    }
  }

  private static List<MatcherPathElement> getPath(Machine machine) {
    List<MatcherPathElement> list = Lists.newArrayList();
    int endIndex = machine.getIndex();
    MachineStack stack = machine.peek();
    while (!stack.isEmpty()) {
      if (stack.matcher() instanceof MutableParsingRule) {
        list.add(new MatcherPathElement(stack.matcher(), stack.index(), endIndex));
        endIndex = stack.index();
      }
      stack = stack.parent();
    }
    return ImmutableList.copyOf(Lists.reverse(list));
  }

  private boolean isNewPath(List<MatcherPathElement> path) {
    for (List<MatcherPathElement> old : Lists.reverse(failedPaths)) {
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
      if (!first.get(i).equals(second.get(i))) {
        return false;
      }
    }
    return true;
  }

}
