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

    int firstLexerIndex = parsingState.lexerIndex;

    // Loop in a pending recursion
    if (parsingState.hasPendingLeftRecursion() && partialAstNodes.containsKey(firstLexerIndex)) {
      parsingState.stopLeftRecursion();
      return partialAstNodes.get(firstLexerIndex);
    }

    // Stop recursion When :
    // 1   - This rule is already in the parsing stack
    // 2.A - The previous rule in the parsing stack has begun at the same token index
    // 2.B - Or another left recursion rule is currently trying to do some recursion at the same token index
    if ( !matchStartIndexes.isEmpty()
        && (matchStartIndexes.peek() == firstLexerIndex || (parsingState.hasPendingLeftRecursion() && matchStartIndexes.peek() == parsingState.lastRecursionLexerIndex))) {
      throw RecognitionExceptionImpl.create();
    }

    try {
      matchStartIndexes.push(firstLexerIndex);

      AstNode currentNode = super.match(parsingState);

      int previousLexerIndex = firstLexerIndex;
      try {
        while (previousLexerIndex < parsingState.lexerIndex) {
          previousLexerIndex = parsingState.lexerIndex;
          parsingState.lastRecursionLexerIndex = firstLexerIndex;
          partialAstNodes.put(parsingState.lexerIndex, currentNode);
          parsingState.startLeftRecursion();
          currentNode = super.match(parsingState);
        }
      } catch (RecognitionExceptionImpl e) {
        partialAstNodes.remove(parsingState.lexerIndex);
        parsingState.stopLeftRecursion();
      }

      return currentNode;
    } finally {
      matchStartIndexes.pop();
      partialAstNodes.remove(firstLexerIndex);
    }
  }

  public void endParsing() {
    matchStartIndexes = new Stack<Integer>();
    partialAstNodes = new HashMap<Integer, AstNode>();
  }
}
