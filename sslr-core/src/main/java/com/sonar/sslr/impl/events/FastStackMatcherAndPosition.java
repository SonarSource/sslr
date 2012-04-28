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

import java.util.Stack;

import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public final class FastStackMatcherAndPosition {

  private static final int INITIAL_CAPACITY = 800;

  private Matcher[] matchers;
  private int[] fromIndex;
  private int[] toIndex;
  private int i;

  public FastStackMatcherAndPosition() {
    this.matchers = new Matcher[INITIAL_CAPACITY];
    this.fromIndex = new int[INITIAL_CAPACITY];
    this.toIndex = new int[INITIAL_CAPACITY];
    this.i = 0;
  }

  public static void copyOnlyRuleMatchers(FastStackMatcherAndPosition result, FastStackMatcherAndPosition source, Stack<Integer> ruleIndexes) {
    int oldSize = result.i;

    if (result.matchers.length < ruleIndexes.size()) {
      result.matchers = new Matcher[source.matchers.length];
      result.fromIndex = new int[source.fromIndex.length];
      result.toIndex = new int[source.toIndex.length];
    }

    int i = 0;
    for (Integer ruleIndex : ruleIndexes) {
      result.matchers[i] = source.matchers[ruleIndex];
      result.fromIndex[i] = source.fromIndex[ruleIndex];
      result.toIndex[i] = source.toIndex[ruleIndex];
      i++;
    }
    result.i = i;

    while (i < oldSize) {
      result.matchers[i] = null; // Allow the Garbage Collector to do its job
      i++;
    }
  }

  public int peek() {
    return i - 1;
  }

  public Matcher peekMatcher() {
    return matchers[i - 1];
  }

  public RuleMatcher peekRule() {
    return (RuleMatcher) matchers[i - 1];
  }

  public int peekFromIndex() {
    return fromIndex[i - 1];
  }

  public int peekToIndex() {
    return toIndex[i - 1];
  }

  public void pop() {
    i--;
    matchers[i] = null; // Allow the Garbage Collector to do its job
  }

  public Matcher getMatcher(int i) {
    return matchers[i];
  }

  public RuleMatcher getRule(int i) {
    return (RuleMatcher) matchers[i];
  }

  public int getFromIndex(int i) {
    return fromIndex[i];
  }

  public int getToIndex(int i) {
    return toIndex[i];
  }

  public void setToIndex(int i, int value) {
    toIndex[i] = value;
  }

  public void push(Matcher matcher, int fromIndex) {
    if (i >= matchers.length) {
      Matcher[] newMatchers = new Matcher[matchers.length * 2];
      System.arraycopy(matchers, 0, newMatchers, 0, matchers.length);
      matchers = newMatchers;

      int[] newFromIndex = new int[this.fromIndex.length * 2];
      System.arraycopy(this.fromIndex, 0, newFromIndex, 0, this.fromIndex.length);
      this.fromIndex = newFromIndex;

      int[] newToIndex = new int[this.toIndex.length * 2];
      System.arraycopy(this.toIndex, 0, newToIndex, 0, this.toIndex.length);
      this.toIndex = newToIndex;
    }

    matchers[i] = matcher;
    this.fromIndex[i] = fromIndex;
    i++;
  }

  public int size() {
    return i;
  }

  public void clear() {
    while (--i >= 0) {
      matchers[i] = null; // Allow the Garbage Collector to do its job
    }
    i = 0;
  }

}
