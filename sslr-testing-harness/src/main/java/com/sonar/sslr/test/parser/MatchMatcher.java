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
package com.sonar.sslr.test.parser;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.ParsingStackTrace;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;

class MatchMatcher extends BaseMatcher<Matcher> {

  private final String sourceCode;
  private final Lexer lexer;
  private String parsingStackTrace;
  private Matcher matcher;

  public MatchMatcher(String sourceCode, Lexer lexer) {
    this.sourceCode = sourceCode;
    this.lexer = lexer;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof Matcher)) {
      return false;
    }
    matcher = (Matcher) obj;
    ParsingState parsingState = new ParsingState(lexer.lex(sourceCode));
    try {
      matcher.match(parsingState);
      return true;
    } catch (BacktrackingEvent e) {
      parsingStackTrace = ParsingStackTrace.generateFullStackTrace(parsingState);
      return false;
    }
  }

  public void describeTo(Description desc) {
    if (parsingStackTrace != null) {
      desc.appendText("The matcher '" + matcher + "' doesn't match the beginning of '" + sourceCode + "'.\n");
      desc.appendText("Parsing stack trace : " + parsingStackTrace);
    } else {
      desc.appendText("The matcher '" + matcher + "' hasn't matched the overall expression '" + sourceCode + "'.\n");
    }
  }
}
