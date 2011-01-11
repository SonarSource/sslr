/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class LeftRecursiveRuleImpl extends RuleImpl {

  private Stack<Integer> matchStartIndexes = new Stack<Integer>();
  private Map<Integer, AstNode> partialAstNodes = new HashMap<Integer, AstNode>();
  private Set<Integer> recursionSignals = new HashSet<Integer>();

  public LeftRecursiveRuleImpl(String name) {
    super(name);
  }

  @Override
  public AstNode match(ParsingState parsingState) {

    int firstLexerIndex = parsingState.lexerIndex;

    // Loop in a pending recursion
    if (partialAstNodes.containsKey(firstLexerIndex)) {
      parsingState.allowToPopToken();
      return partialAstNodes.remove(firstLexerIndex);
    }

    // Stop recursion
    if ( !matchStartIndexes.isEmpty() && matchStartIndexes.peek() == firstLexerIndex) {
      recursionSignals.add(firstLexerIndex);
      throw RecognitionExceptionImpl.create();
    }

    try {
      matchStartIndexes.push(firstLexerIndex);

      AstNode currentNode = super.match(parsingState);

      // Relaunch matching in case of recursion
      if (recursionSignals.contains(firstLexerIndex)) {
        try {
          while (true) {
            partialAstNodes.put(parsingState.lexerIndex, currentNode);
            parsingState.forbidToPopToken();
            currentNode = super.match(parsingState);
          }
        } catch (RecognitionExceptionImpl e) {
          partialAstNodes.remove(parsingState.lexerIndex);
          parsingState.allowToPopToken();
        }
      }

      return currentNode;
    } finally {
      matchStartIndexes.pop();
      recursionSignals.remove(firstLexerIndex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startParsing(ParsingState parsingState) {
    super.notifyStartParsing(parsingState);
    matchStartIndexes = new Stack<Integer>();
    partialAstNodes = new HashMap<Integer, AstNode>();
    recursionSignals = new HashSet<Integer>();
  }

}
