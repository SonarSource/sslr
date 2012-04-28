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
package com.sonar.sslr.impl.events;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Stack;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public final class ExtendedStackTrace extends ParsingEventListener {

  public final FastStackMatcherAndPosition currentStack = new FastStackMatcherAndPosition();
  public final FastStackMatcherAndPosition longestStack = new FastStackMatcherAndPosition();
  private final Stack<Integer> currentStackRules = new Stack<Integer>();
  public Matcher longestOutertMatcher;
  public Matcher longestMatcher;
  public int longestIndex;
  public ParsingState longestParsingState;

  @Override
  public void beginParse() {
    currentStack.clear();
    longestStack.clear();
    currentStackRules.clear();
    longestIndex = -1;
  }

  @Override
  public void enterRule(RuleMatcher rule, ParsingState parsingState) {
    /* The beginning of a rule is the "end" (when partitioning) of the last one, so update the last's one toIndex */
    int lastRuleWithPosition = getLastRuleWithPosition(currentStack);
    if (lastRuleWithPosition != -1) {
      currentStack.setToIndex(lastRuleWithPosition, parsingState.lexerIndex);
    }

    /* Add the newly entered rule to the current stack */
    currentStackRules.push(currentStack.size());
    currentStack.push(rule, parsingState.lexerIndex);
  }

  @Override
  public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
    currentStackRules.pop();
    currentStack.pop();
  }

  @Override
  public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState) {
    currentStackRules.pop();
    currentStack.pop();
  }

  @Override
  public void enterMatcher(Matcher matcher, ParsingState parsingState) {
    currentStack.push(matcher, parsingState.lexerIndex);
  }

  @Override
  public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    currentStack.pop();
  }

  @Override
  public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState) {
    /* Handle the longest path */
    if (enforceLexerIndexUpperBound(parsingState.lexerIndex, parsingState.lexerSize) > longestIndex) {
      /* New longest path! */
      longestIndex = enforceLexerIndexUpperBound(parsingState.lexerIndex, parsingState.lexerSize);
      longestMatcher = currentStack.peekMatcher();

      /* Set the longest matcher to the outer most one starting at the same index as the current matcher */
      longestOutertMatcher = getOuterMatcher(currentStack, currentStack.peekFromIndex());

      /* Set the current's rule toIndex */
      int lastRuleWithPosition = getLastRuleWithPosition(currentStack);
      if (lastRuleWithPosition != -1) {
        currentStack.setToIndex(lastRuleWithPosition, parsingState.lexerIndex);
      }

      /* Copy the current stack to the longest stack (deep-copy / clone) */
      FastStackMatcherAndPosition.copyOnlyRuleMatchers(longestStack, currentStack, currentStackRules);

      this.longestParsingState = parsingState;
    }

    currentStack.pop();
  }

  private static int enforceLexerIndexUpperBound(int index, int lexerSize) {
    return index < lexerSize ? index : lexerSize - 1;
  }

  private static int getLastRuleWithPosition(FastStackMatcherAndPosition stack) {
    for (int i = stack.size() - 1; i >= 0; i--) {
      if (stack.getMatcher(i) instanceof RuleMatcher) {
        return i;
      }
    }

    return -1;
  }

  private static Matcher getOuterMatcher(FastStackMatcherAndPosition stack, int lexerIndex) {
    for (int i = 0; i < stack.size(); i++) {
      if (stack.getFromIndex(i) == lexerIndex) {
        return stack.getMatcher(i);
      }
    }

    return null;
  }

  @Override
  public String toString() {
    PrintStream stream = null;

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      stream = new PrintStream(baos);
      ExtendedStackTraceStream.print(this, stream);
      return baos.toString();
    } finally {
      if (stream != null) {
        stream.close();
      }
    }
  }

}
