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
package com.sonar.sslr.impl;

import com.google.common.collect.Sets;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.RecognitionExceptionListener;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import com.sonar.sslr.impl.events.ParsingEventListener;

import java.util.List;
import java.util.Set;

/**
 * @deprecated in 1.19
 */
@Deprecated
public class ParsingState {

  private final Token[] tokens;

  public int lexerIndex = 0;
  public final int lexerSize;

  private int outpostMatcherTokenIndex = -1;
  private final Set<RecognitionExceptionListener> listeners = Sets.newHashSet();
  public ParsingEventListener[] parsingEventListeners;
  public ExtendedStackTrace extendedStackTrace;

  public ParsingState(List<Token> tokens) {
    this.tokens = tokens.toArray(new Token[tokens.size()]);
    lexerSize = this.tokens.length;
  }

  public final boolean hasNextToken() {
    return lexerIndex < lexerSize;
  }

  public Token readToken(int tokenIndex) {
    if (tokenIndex >= tokens.length) {
      return null;
    }
    return tokens[tokenIndex];
  }

  public Token getOutpostMatcherToken() {
    if (outpostMatcherTokenIndex >= lexerSize || outpostMatcherTokenIndex == -1) {
      return null;
    }
    return tokens[outpostMatcherTokenIndex];
  }

  public final void addListeners(RecognitionExceptionListener... listeners) {
    for (RecognitionExceptionListener listener : listeners) {
      this.listeners.add(listener);
    }
  }

  public final void notifyListeners(RecognitionException recognitionException) {
    for (RecognitionExceptionListener listener : listeners) {
      listener.processRecognitionException(recognitionException);
    }
  }

}
