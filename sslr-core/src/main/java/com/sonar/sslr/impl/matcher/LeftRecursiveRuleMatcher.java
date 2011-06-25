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
import com.sonar.sslr.impl.BacktrackingEvent;

/**
 * Implementation of a Rule that support left recursion.
 */
public class LeftRecursiveRuleMatcher extends RuleMatcher {

  private Stack<Integer> matchStartIndexes = new Stack<Integer>();
  private Map<Integer, AstNode> partialAstNodes = new HashMap<Integer, AstNode>();

  protected LeftRecursiveRuleMatcher(String name) {
    super(name);
  }
  
  @Override
  public AstNode matchWorker(ParsingState parsingState) {

    int mostLeftLexerIndex = parsingState.lexerIndex;

    // Loop in a pending recursion
    if ( !matchStartIndexes.isEmpty() && partialAstNodes.containsKey(mostLeftLexerIndex)) {
      AstNode partialAstNode = partialAstNodes.get(mostLeftLexerIndex);
      parsingState.lexerIndex = partialAstNode.getToIndex();
      parsingState.stopLeftRecursion();
      return partialAstNode;
    }

    // Stop recursion When this rule is already in the parsing stack
    if ( !matchStartIndexes.isEmpty() && (matchStartIndexes.peek() == mostLeftLexerIndex)) {
      throw BacktrackingEvent.create();
    }

    try {
      matchStartIndexes.push(mostLeftLexerIndex);

      AstNode currentNode = super.matchWorker(parsingState);

      int mostRightLexerIndex = mostLeftLexerIndex;
      try {
        while (mostRightLexerIndex < parsingState.lexerIndex) {
          mostRightLexerIndex = parsingState.lexerIndex;
          currentNode.setToIndex(parsingState.lexerIndex);
          parsingState.lexerIndex = mostLeftLexerIndex;
          parsingState.startLeftRecursion();
          partialAstNodes.put(mostLeftLexerIndex, currentNode);
          currentNode = super.matchWorker(parsingState);
        }
      } catch (BacktrackingEvent e) {
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

  @Override
  public void recoveryRule() {
    throw new UnsupportedOperationException("The recovery mode is not yet supported by left-recursive rule");
  }
}
