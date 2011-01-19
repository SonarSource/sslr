/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.loggers.SslrLogger;

/**
 * Implementation of a Rule that support left recursion.
 */
public class LeftRecursiveRuleImpl extends RuleImpl {

  private Stack<Integer> matchStartIndexes = new Stack<Integer>();
  private Map<Integer, AstNode> partialAstNodes = new HashMap<Integer, AstNode>();

  public LeftRecursiveRuleImpl(String name) {
    super(name);
  }

  @Override
  public AstNode match(ParsingState parsingState) {

    int mostLeftLexerIndex = parsingState.lexerIndex;

    // Loop in a pending recursion
    if ( !matchStartIndexes.isEmpty() && partialAstNodes.containsKey(mostLeftLexerIndex)) {
      AstNode partialAstNode = partialAstNodes.get(mostLeftLexerIndex);
      parsingState.lexerIndex = partialAstNode.getToIndex();
      parsingState.stopLeftRecursion();
      SslrLogger.hasMatchedWithLeftRecursion(this, parsingState, partialAstNode);
      return partialAstNode;
    }

    // Stop recursion When this rule is already in the parsing stack
    if ( !matchStartIndexes.isEmpty() && (matchStartIndexes.peek() == mostLeftLexerIndex)) {
      SslrLogger.stopLeftRecursion(this, parsingState);
      throw RecognitionExceptionImpl.create();
    }

    try {
      matchStartIndexes.push(mostLeftLexerIndex);

      AstNode currentNode = super.match(parsingState);

      int mostRightLexerIndex = mostLeftLexerIndex;
      try {
        while (mostRightLexerIndex < parsingState.lexerIndex) {
          mostRightLexerIndex = parsingState.lexerIndex;
          currentNode.setToIndex(parsingState.lexerIndex);
          parsingState.lexerIndex = mostLeftLexerIndex;
          parsingState.startLeftRecursion();
          partialAstNodes.put(mostLeftLexerIndex, currentNode);
          currentNode = super.match(parsingState);
        }
      } catch (RecognitionExceptionImpl e) {
      } finally {
        parsingState.lexerIndex = mostRightLexerIndex;
        parsingState.stopLeftRecursion();
      }

      return currentNode;
    } finally {
      matchStartIndexes.pop();
      partialAstNodes.remove(mostLeftLexerIndex);
    }
  }

  public void endParsing() {
    matchStartIndexes = new Stack<Integer>();
    partialAstNodes = new HashMap<Integer, AstNode>();
  }
}
