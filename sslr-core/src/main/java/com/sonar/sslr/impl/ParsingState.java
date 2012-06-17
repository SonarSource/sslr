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
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.RecognitionExceptionListener;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import com.sonar.sslr.impl.events.ParsingEventListener;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MemoizedMatcher;

import java.util.List;
import java.util.Set;

public class ParsingState {

  private final Token[] tokens;

  public int lexerIndex = 0;
  public final int lexerSize;

  private int outpostMatcherTokenIndex = -1;
  private Matcher outpostMatcher;

  private final Set<RecognitionExceptionListener> listeners = Sets.newHashSet();
  private final AstNode[] astNodeMemoization;
  private final MemoizedMatcher[] astMatcherMemoization;
  public ParsingEventListener[] parsingEventListeners;
  public ExtendedStackTrace extendedStackTrace;

  public ParsingState(List<Token> tokens) {
    this.tokens = tokens.toArray(new Token[tokens.size()]);
    lexerSize = this.tokens.length;
    astNodeMemoization = new AstNode[lexerSize + 1];
    astMatcherMemoization = new MemoizedMatcher[lexerSize + 1];
  }

  /**
   * @throws BacktrackingEvent when there is no next token
   */
  public final Token popToken(Matcher matcher) {
    if (lexerIndex >= outpostMatcherTokenIndex) {
      outpostMatcherTokenIndex = lexerIndex;
      outpostMatcher = matcher;
    }
    if (lexerIndex >= lexerSize) {
      throw BacktrackingEvent.create();
    }
    return tokens[lexerIndex++];
  }

  public final boolean hasNextToken() {
    return lexerIndex < lexerSize;
  }

  /**
   * @throws BacktrackingEvent when there is no next token
   */
  public final Token peekToken(int index, Matcher matcher) {
    if (index > outpostMatcherTokenIndex) {
      outpostMatcherTokenIndex = index;
      outpostMatcher = matcher;
    }
    if (index >= lexerSize) {
      throw BacktrackingEvent.create();
    }
    return tokens[index];
  }

  /**
   * @return null, when there is no next token
   */
  public final Token peekTokenIfExists(int index, Matcher matcher) {
    // Note that implementation almost the same as in peekToken
    if (index > outpostMatcherTokenIndex) {
      outpostMatcherTokenIndex = index;
      outpostMatcher = matcher;
    }
    if (index >= lexerSize) {
      return null;
    }
    return tokens[index];
  }

  /**
   * @throws BacktrackingEvent when there is no next token
   */
  public final Token peekToken(Matcher matcher) {
    return peekToken(lexerIndex, matcher);
  }

  public Token readToken(int tokenIndex) {
    if (tokenIndex >= tokens.length) {
      return null;
    }
    return tokens[tokenIndex];
  }

  public final Matcher getOutpostMatcher() {
    return outpostMatcher;
  }

  public Token getOutpostMatcherToken() {
    if (outpostMatcherTokenIndex >= lexerSize || outpostMatcherTokenIndex == -1) {
      return null;
    }
    return tokens[outpostMatcherTokenIndex];
  }

  public final int getOutpostMatcherTokenIndex() {
    return outpostMatcherTokenIndex;
  }

  public final int getOutpostMatcherTokenLine() {
    if (outpostMatcherTokenIndex < lexerSize) {
      return tokens[outpostMatcherTokenIndex].getLine();
    }
    return tokens[lexerSize - 1].getLine();
  }

  public void memoizeAst(MemoizedMatcher matcher, AstNode astNode) {
    astNode.setToIndex(lexerIndex);
    astNodeMemoization[astNode.getFromIndex()] = astNode;
    astMatcherMemoization[astNode.getFromIndex()] = matcher;
  }

  public final void deleteMemoizedAstAfter(int index) {
    for (int i = index; i <= outpostMatcherTokenIndex; i++) {
      astMatcherMemoization[i] = null;
      astNodeMemoization[i] = null;
    }
  }

  public boolean hasMemoizedAst(MemoizedMatcher matcher) {
    if (astMatcherMemoization[lexerIndex] == matcher) {
      return true;
    }
    return false;
  }

  public AstNode getMemoizedAst(MemoizedMatcher matcher) {
    if (hasMemoizedAst(matcher)) {
      return astNodeMemoization[lexerIndex];
    }
    return null;
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
