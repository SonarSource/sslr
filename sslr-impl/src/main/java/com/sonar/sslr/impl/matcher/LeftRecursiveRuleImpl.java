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

  private Stack<Integer> ruleCalledAtIndexes = new Stack<Integer>();
  private Stack<Integer> recursionDetectedIndexes = new Stack<Integer>();

  public LeftRecursiveRuleImpl(String name) {
    super(name);
  }

  @Override
  public AstNode match(ParsingState parsingState) {
    // printState("-- Start Match", parsingState);

    if ( !ruleCalledAtIndexes.isEmpty() && ruleCalledAtIndexes.peek().equals(parsingState.lexerIndex)) {
      recursionDetectedIndexes.push(parsingState.lexerIndex);

      // printState("---- /!\\ Recursion detected", parsingState);
      throw RecognitionExceptionImpl.create();
    }

    ruleCalledAtIndexes.push(parsingState.lexerIndex);

    AstNode currentNode = null;
    try {

      // printState("-- Before inner Match", parsingState);

      currentNode = super.match(parsingState);

      // printState("-- After inner Match", parsingState);

    } catch (RecognitionExceptionImpl e) {
      throw e;
    } finally {
      ruleCalledAtIndexes.pop();
      recursionDetectedIndexes.pop();
    }

    // printState("-- End Match", parsingState);
    return currentNode;
  }

  private void printState(String message, ParsingState parsingState) {
    StringBuffer state = new StringBuffer(message);
    state.append(" = lexingIndex: ");
    state.append(parsingState.lexerIndex);
    state.append(" | called: ");
    state.append(ruleCalledAtIndexes);
    state.append(" | detected: ");
    state.append(recursionDetectedIndexes);
    System.out.println(state.toString());
  }

}
