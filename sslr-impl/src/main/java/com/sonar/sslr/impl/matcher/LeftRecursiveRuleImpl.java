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
    if (partialAstNodes.containsKey(firstLexerIndex)) {
      parsingState.stopLeftRecursion();
      return partialAstNodes.get(firstLexerIndex);
    }

    // Stop recursion
    if ( !matchStartIndexes.isEmpty() && matchStartIndexes.peek() == firstLexerIndex) {
      throw RecognitionExceptionImpl.create();
    }

    try {
      matchStartIndexes.push(firstLexerIndex);

      AstNode currentNode = super.match(parsingState);

      int previousLexerIndex = firstLexerIndex;
      try {
        while (previousLexerIndex != parsingState.lexerIndex) {
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
