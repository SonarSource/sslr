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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MemoizedMatcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public abstract class ParsingEventListener {

  /* Parsing level */
  public void beginLex() {
  };

  public void endLex() {
  };

  public void beginParse() {
  };

  public void endParse() {
  };

  /* Rule level */
  public void enterRule(RuleMatcher rule, ParsingState parsingState) {
  };

  public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
  };

  public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState) {
  };

  /* Matcher level */
  public void enterMatcher(Matcher matcher, ParsingState parsingState) {
  };

  public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
  };

  public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState) {
  };

  /* Memoization level */
  public void memoizerHit(MemoizedMatcher matcher, ParsingState parsingState) {
  };

  public void memoizerMiss(MemoizedMatcher matcher, ParsingState parsingState) {
  };
}
