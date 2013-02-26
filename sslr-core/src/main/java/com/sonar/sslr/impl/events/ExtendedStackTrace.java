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
import com.sonar.sslr.impl.matcher.RuleMatcher;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @deprecated in 1.19
 */
@Deprecated
public final class ExtendedStackTrace extends ParsingEventListener {

  public Matcher longestOutertMatcher;
  public Matcher longestMatcher;
  public int longestIndex;
  public ParsingState longestParsingState;

  @Override
  public void beginParse() {
  }

  @Override
  public void enterRule(RuleMatcher rule, ParsingState parsingState) {
  }

  @Override
  public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
  }

  @Override
  public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState) {
  }

  @Override
  public void enterMatcher(Matcher matcher, ParsingState parsingState) {
  }

  @Override
  public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
  }

  @Override
  public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState) {
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
