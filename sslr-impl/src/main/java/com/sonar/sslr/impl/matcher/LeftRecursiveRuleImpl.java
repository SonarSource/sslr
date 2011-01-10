/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class LeftRecursiveRuleImpl extends RuleImpl {

  private int lastStartIndex = -1;
  private boolean recursionSignal = false;
  private AstNode partialAstNode;

  public LeftRecursiveRuleImpl(String name) {
    super(name);
  }

  @Override
  public AstNode match(ParsingState parsingState) {

    // Loop in a pending recursion
    if (partialAstNode != null) {
      AstNode returnAstNode = partialAstNode;
      partialAstNode = null;
      return returnAstNode;
    }

    // Stop recursion
    if (lastStartIndex == parsingState.lexerIndex) {
      recursionSignal = true;
      throw RecognitionExceptionImpl.create();
    }

    lastStartIndex = parsingState.lexerIndex;

    AstNode currentNode = super.match(parsingState);

    // Relaunch matching in case of recursion
    if (recursionSignal) {
      try {
        while (true) {
          partialAstNode = currentNode;
          currentNode = super.match(parsingState);
        }
      } catch (RecognitionExceptionImpl e) {
        recursionSignal = false;
        partialAstNode = null;
      }
    }

    return currentNode;
  }

}
