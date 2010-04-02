/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser;

import java.util.List;

import com.sonarsource.parser.ast.AstNode;
import com.sonarsource.parser.matcher.Matcher;
import com.sonarsource.sslr.api.Token;

public class ParsingState {

  private final Token[] tokens;
  public int lexerIndex = 0;
  public final int lexerSize;
  private int outpostMatcherTokenIndex = -1;
  private Matcher outpostMatcher;
  private AstNode[] astNodeMemoization;
  private Matcher[] astMatcherMemoization;

  public ParsingState(List<Token> tokens) {
    this.tokens = tokens.toArray(new Token[0]);
    lexerSize = this.tokens.length;
    astNodeMemoization = new AstNode[lexerSize + 1];
    astMatcherMemoization = new Matcher[lexerSize + 1];
  }

  public Token popToken(Matcher matcher) {
    if (lexerIndex > outpostMatcherTokenIndex || lexerIndex == 0) {
      outpostMatcherTokenIndex = lexerIndex;
      outpostMatcher = matcher;
    }
    if (lexerIndex >= lexerSize) {
      throw RecognitionException.create();
    }
    return tokens[lexerIndex++];
  }

  public boolean hasNextToken() {
    return lexerIndex < lexerSize;
  }

  public Token peekToken(int index, Matcher matcher) {
    if (index > outpostMatcherTokenIndex) {
      outpostMatcherTokenIndex = index;
      outpostMatcher = matcher;
    }
    if (index >= lexerSize) {
      throw RecognitionException.create();
    }
    return tokens[index];
  }

  public Token peekToken(Matcher matcher) {
    return peekToken(lexerIndex, matcher);
  }

  public Matcher getOutpostMatcher() {
    return outpostMatcher;
  }

  public Token getOutpostMatcherToken() {
    if (outpostMatcherTokenIndex >= lexerSize) {
      return null;
    }
    return tokens[outpostMatcherTokenIndex];
  }

  public void memoizeAst(Matcher matcher, AstNode astNode) {
    astNode.setToIndex(lexerIndex);
    astNodeMemoization[astNode.getFromIndex()] = astNode;
    astMatcherMemoization[astNode.getFromIndex()] = matcher;
  }

  public boolean hasMemoizedAst(Matcher matcher) {
    if (astMatcherMemoization[lexerIndex] == matcher) {
      return true;
    }
    return false;
  }

  public AstNode getMemoizedAst(Matcher matcher) {
    AstNode astNode = null;
    if (hasMemoizedAst(matcher)) {
      astNode = astNodeMemoization[lexerIndex];
      lexerIndex = astNode.getToIndex();
    }
    return astNode;
  }

  public Token peekTokenIfExists(int index, Matcher matcher) {
    try {
      return peekToken(index, matcher);
    } catch (RecognitionException e) {
      return null;
    }
  }
}
