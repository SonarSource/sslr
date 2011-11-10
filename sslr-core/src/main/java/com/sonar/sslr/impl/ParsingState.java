/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.RecognictionExceptionListener;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.events.ParsingEventListener;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MemoizedMatcher;

public class ParsingState {

  private List<MemoizedMatcher> notifiedMatchers = Lists.newArrayList();
  private Set<RecognictionExceptionListener> listeners = new HashSet<RecognictionExceptionListener>();
  private final Token[] tokens;
  public int lexerIndex = 0;
  public int lastRecursionLexerIndex = 0;
  public final int lexerSize;
  private int outpostMatcherTokenIndex = -1;
  private Matcher outpostMatcher;
  private AstNode[] astNodeMemoization;
  private MemoizedMatcher[] astMatcherMemoization;
  private boolean pendingLeftRecursion = false;
  public ParsingEventListener[] parsingEventListeners;

  public ParsingState(List<Token> tokens) {
    this.tokens = tokens.toArray(new Token[tokens.size()]);
    lexerSize = this.tokens.length;
    astNodeMemoization = new AstNode[lexerSize + 1];
    astMatcherMemoization = new MemoizedMatcher[lexerSize + 1];
  }

  public final Token popToken(Matcher matcher) {
    if (pendingLeftRecursion) {
      throw BacktrackingEvent.create();
    }
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

  public final Token peekToken(int index, Matcher matcher) {
    if (pendingLeftRecursion) {
      throw BacktrackingEvent.create();
    }
    if (index > outpostMatcherTokenIndex) {
      outpostMatcherTokenIndex = index;
      outpostMatcher = matcher;
    }
    if (index >= lexerSize) {
      throw BacktrackingEvent.create();
    }
    return tokens[index];
  }

  public final boolean hasPendingLeftRecursion() {
    return pendingLeftRecursion;
  }

  public final Token peekToken(Matcher matcher) {
    return peekToken(lexerIndex, matcher);
  }

  public final Token readToken(int tokenIndex) {
    if (tokenIndex >= tokens.length) {
      return null;
    }
    return tokens[tokenIndex];
  }

  public final Matcher getOutpostMatcher() {
    return outpostMatcher;
  }

  public final Token getOutpostMatcherToken() {
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

  public final void memoizeAst(MemoizedMatcher matcher, AstNode astNode) {
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

  public final boolean hasMemoizedAst(MemoizedMatcher matcher) {
    if ( !pendingLeftRecursion && astMatcherMemoization[lexerIndex] == matcher) {
      return true;
    }
    return false;
  }

  public final AstNode getMemoizedAst(MemoizedMatcher matcher) {
    if (hasMemoizedAst(matcher)) {
      return astNodeMemoization[lexerIndex];
    }
    return null;
  }

  public final Token peekTokenIfExists(int index, Matcher matcher) {
    try {
      return peekToken(index, matcher);
    } catch (BacktrackingEvent e) {
      return null;
    }
  }

  public final void startLeftRecursion() {
    pendingLeftRecursion = true;
  }

  public final void stopLeftRecursion() {
    pendingLeftRecursion = false;
  }

  public final void reinitNotifiedMatchersList() {
    notifiedMatchers.clear();
  }

  public final void matcherNotified(MemoizedMatcher matcher) {
    notifiedMatchers.add(matcher);
  }

  public final boolean hasMatcherBeenNotified(MemoizedMatcher matcher) {
    return notifiedMatchers.contains(matcher);
  }

  public final void setLeftRecursionState(boolean leftRecursionState) {
    this.pendingLeftRecursion = leftRecursionState;
  }

  protected final void setListeners(Set<RecognictionExceptionListener> listeners) {
    this.listeners = listeners;
  }

  public final void addListener(RecognictionExceptionListener listener) {
    listeners.add(listener);
  }

  public final void notifyListerners(RecognitionException recognitionException) {
    for (RecognictionExceptionListener listener : listeners) {
      listener.processRecognitionException(recognitionException);
    }
  }
}
