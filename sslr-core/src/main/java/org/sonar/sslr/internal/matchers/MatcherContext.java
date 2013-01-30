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

public abstract class MatcherContext implements CharSequence {

  protected InputBuffer inputBuffer;
  protected int startIndex;
  protected int currentIndex;
  protected Matcher matcher;
  protected boolean ignoreErrors = false;

  private final MatchHandler matchHandler;

  public MatcherContext(InputBuffer inputBuffer, MatchHandler matchHandler) {
    this.inputBuffer = inputBuffer;
    this.matchHandler = matchHandler;
  }

  public abstract MatcherContext getSubContext(Matcher matcher);

  public abstract void createNode();

  public abstract void createNode(ParseNode parseNode);

  public abstract void skipNode();

  public MatchHandler getMatchHandler() {
    return matchHandler;
  }

  public Matcher getMatcher() {
    return matcher;
  }

  public void retire() {
    this.matcher = null;
  }

  public void ignoreErrors() {
    this.ignoreErrors = true;
  }

  public boolean isIgnoreErrors() {
    return ignoreErrors;
  }

  public abstract boolean runMatcher();

  public int getStartIndex() {
    return startIndex;
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public void advanceIndex(int delta) {
    currentIndex += delta;
  }

  public void resetIndex() {
    currentIndex = startIndex;
  }

  public void setIndex(int index) {
    currentIndex = index;
  }

  public abstract ParseNode getNode();

  /*
   * CharSequence
   */

  public int length() {
    return inputBuffer.length() - currentIndex;
  }

  public char charAt(int index) {
    return inputBuffer.charAt(currentIndex + index);
  }

  public CharSequence subSequence(int start, int end) {
    throw new UnsupportedOperationException();
  }

}
