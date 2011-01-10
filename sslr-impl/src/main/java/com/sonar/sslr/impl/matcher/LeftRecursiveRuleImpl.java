/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.Stack;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class LeftRecursiveRuleImpl extends RuleImpl {

  private Stack<Integer> matchStartIndex = new Stack<Integer>();
  private boolean recursionSignal = false;
  private AstNode partialAstNode;

  public LeftRecursiveRuleImpl(String name) {
    super(name);
  }

  @Override
  public AstNode match(ParsingState parsingState) {

    int startIndex = parsingState.lexerIndex;

    // Loop in a pending recursion
    if (partialAstNode != null) {
      AstNode returnAstNode = partialAstNode;
      partialAstNode = null;
      return returnAstNode;
    }

    // Stop recursion
    if ( !matchStartIndex.isEmpty() && matchStartIndex.peek().equals(startIndex)) {
      recursionSignal = true;
      throw RecognitionExceptionImpl.create();
    }

    matchStartIndex.push(startIndex);

    AstNode currentNode = null;
    try {
      currentNode = super.match(parsingState);

      // Relaunch matching in case of recursion
      while (recursionSignal) {
        partialAstNode = currentNode;
        try {
          currentNode = super.match(parsingState);
        } catch (RecognitionExceptionImpl e) {
          recursionSignal = false;
          partialAstNode = null;
        }
      }
    } catch (RecognitionExceptionImpl e) {
      throw e;
    } finally {
      matchStartIndex.pop();
    }

    return currentNode;
  }

}
