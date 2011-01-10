/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.Stack;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.LeftRecursionDetectedException;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class LeftRecursiveRuleImpl extends RuleImpl {

  private Stack<Integer> recursionDetectedIndexes = new Stack<Integer>();

  public LeftRecursiveRuleImpl(String name) {
    super(name);
  }

  @Override
  public AstNode match(ParsingState parsingState) {
    if ( !recursionDetectedIndexes.isEmpty() && recursionDetectedIndexes.peek().equals(parsingState.lexerIndex)) {
      throw new LeftRecursionDetectedException("Recursion detected at token #" + parsingState.lexerIndex);
    }

    recursionDetectedIndexes.push(parsingState.lexerIndex);

    AstNode currentNode = null;
    try {
      currentNode = super.match(parsingState);
    } catch (RecognitionExceptionImpl e) {
      throw e;
    } finally {
      recursionDetectedIndexes.pop();
    }

    return currentNode;
  }

}
