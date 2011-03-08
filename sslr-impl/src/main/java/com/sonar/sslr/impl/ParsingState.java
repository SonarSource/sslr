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
import com.sonar.sslr.impl.loggers.SslrLogger;
import com.sonar.sslr.impl.matcher.Matcher;

public class ParsingState {

  private List<Matcher> notifiedMatchers = Lists.newArrayList();
  private Set<RecognictionExceptionListener> listeners = new HashSet<RecognictionExceptionListener>();
  private final Token[] tokens;
  public int lexerIndex = 0;
  public int lastRecursionLexerIndex = 0;
  public final int lexerSize;
  private int outpostMatcherTokenIndex = -1;
  private Matcher outpostMatcher;
  private AstNode[] astNodeMemoization;
  private Matcher[] astMatcherMemoization;
  private boolean pendingLeftRecursion = false;

  public ParsingState(List<Token> tokens) {
    this.tokens = tokens.toArray(new Token[0]);
    lexerSize = this.tokens.length;
    astNodeMemoization = new AstNode[lexerSize + 1];
    astMatcherMemoization = new Matcher[lexerSize + 1];
  }

  public Token popToken(Matcher matcher) {
    if (pendingLeftRecursion) {
      throw RecognitionExceptionImpl.create();
    }
    if (lexerIndex >= outpostMatcherTokenIndex) {
      outpostMatcherTokenIndex = lexerIndex;
      outpostMatcher = matcher;
    }
    if (lexerIndex >= lexerSize) {
      throw RecognitionExceptionImpl.create();
    }
    return tokens[lexerIndex++];
  }

  public boolean hasNextToken() {
    return lexerIndex < lexerSize;
  }

  public Token peekToken(int index, Matcher matcher) {
    if (pendingLeftRecursion) {
      throw RecognitionExceptionImpl.create();
    }
    if (index > outpostMatcherTokenIndex) {
      outpostMatcherTokenIndex = index;
      outpostMatcher = matcher;
    }
    if (index >= lexerSize) {
      throw RecognitionExceptionImpl.create();
    }
    return tokens[index];
  }

  public final boolean hasPendingLeftRecursion() {
    return pendingLeftRecursion;
  }

  public Token peekToken(Matcher matcher) {
    return peekToken(lexerIndex, matcher);
  }

  public Token readToken(int tokenIndex) {
    if (tokenIndex >= tokens.length) {
      return null;
    }
    return tokens[tokenIndex];
  }

  public Matcher getOutpostMatcher() {
    return outpostMatcher;
  }

  public Token getOutpostMatcherToken() {
    if (outpostMatcherTokenIndex >= lexerSize || outpostMatcherTokenIndex == -1) {
      return null;
    }
    return tokens[outpostMatcherTokenIndex];
  }

  public int getOutpostMatcherTokenIndex() {
    return outpostMatcherTokenIndex;
  }

  public int getOutpostMatcherTokenLine() {
    if (outpostMatcherTokenIndex < lexerSize) {
      return tokens[outpostMatcherTokenIndex].getLine();
    }
    return tokens[lexerSize - 1].getLine();
  }

  public void memoizeAst(Matcher matcher, AstNode astNode) {
    astNode.setToIndex(lexerIndex);
    astNodeMemoization[astNode.getFromIndex()] = astNode;
    astMatcherMemoization[astNode.getFromIndex()] = matcher;
  }

  public void deleteMemoizedAstAfter(int index) {
    for (int i = index; i <= outpostMatcherTokenIndex; i++) {
      astMatcherMemoization[i] = null;
      astNodeMemoization[i] = null;
    }
  }

  public boolean hasMemoizedAst(Matcher matcher) {
    if ( !pendingLeftRecursion && astMatcherMemoization[lexerIndex] == matcher) {
      return true;
    }
    return false;
  }

  public AstNode getMemoizedAst(Matcher matcher) {
    if (hasMemoizedAst(matcher)) {
      AstNode astNode = astNodeMemoization[lexerIndex];
      SslrLogger.memoizedAstUsed(matcher, this, astNode);
      lexerIndex = astNode.getToIndex();
      return astNode;
    }
    return null;
  }

  public Token peekTokenIfExists(int index, Matcher matcher) {
    try {
      return peekToken(index, matcher);
    } catch (RecognitionExceptionImpl e) {
      return null;
    }
  }

  public void startLeftRecursion() {
    pendingLeftRecursion = true;
  }

  public void stopLeftRecursion() {
    pendingLeftRecursion = false;
  }

  public void reinitNotifiedMatchersList() {
    notifiedMatchers.clear();
  }

  public void matcherNotified(Matcher matcher) {
    notifiedMatchers.add(matcher);
  }

  public boolean hasMatcherBeenNotified(Matcher matcher) {
    return notifiedMatchers.contains(matcher);
  }

  public void setLeftRecursionState(boolean leftRecursionState) {
    this.pendingLeftRecursion = leftRecursionState;
  }

  protected void setListeners(Set<RecognictionExceptionListener> listeners) {
    this.listeners = listeners;
  }

  public void addListener(RecognictionExceptionListener listener) {
    listeners.add(listener);
  }

  public void notifyListerners(RecognitionException recognitionException) {
    for (RecognictionExceptionListener listener : listeners) {
      listener.addRecognitionException(recognitionException);
    }
  }
}
