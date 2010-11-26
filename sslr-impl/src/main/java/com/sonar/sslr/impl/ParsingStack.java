/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import java.util.Stack;

import com.sonar.sslr.impl.matcher.RuleImpl;

public class ParsingStack {

  private Stack<RuleImpl> parsingStack = new Stack<RuleImpl>();

  public void push(RuleImpl rule) {
    parsingStack.push(rule);
  }

  public boolean empty() {
    return parsingStack.empty();
  }

  public RuleImpl pop() {
    return parsingStack.pop();
  }
}
