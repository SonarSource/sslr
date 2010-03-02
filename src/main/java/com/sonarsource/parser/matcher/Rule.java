/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.ast.AstNode;

public class Rule extends Matcher {

  protected String name;
  protected Matcher matcher;
  private boolean hasSeveralParents = false;
  protected boolean hasToBeSkippedWhenBuildingAst = false;

  public Rule(String name) {
    this.name = name;
  }

  public AstNode match(ParsingState parsingState) {
    int startIndex = parsingState.lexerIndex;
    AstNode childNode = matcher.match(parsingState);

    AstNode astNode = new AstNode(this, name, parsingState.peekTokenIfExists(startIndex, matcher), hasToBeSkippedWhenBuildingAst);
    astNode.addChild(childNode);
    return astNode;
  }

  public Rule is(Object... matchers) {
    if (matchers.length == 0) {
      throw new IllegalStateException("The rule '" + name + "' should at least contains one matcher.");
    }
    setMatcher(Matchers.and(matchers));
    return this;
  }

  public void mock() {
    setMatcher(new TokenValueMatcher(name));
  }

  public Rule or(Object... matchers) {
    if (matchers.length == 0) {
      throw new IllegalStateException("A rule should at least contains one matcher.");
    }
    setMatcher(Matchers.or(matchers));
    return this;
  }

  public Rule skip() {
    hasToBeSkippedWhenBuildingAst = true;
    return this;
  }

  protected void setMatcher(Matcher matcher) {
    this.matcher = matcher;
    matcher.setParentRule(this);
  }

  public void setParentRule(Rule parentRule) {
    if (this.parentRule != null && parentRule != this.parentRule) {
      hasSeveralParents = true;
    }
    if (hasSeveralParents) {
      this.parentRule = null;
      return;
    }
    this.parentRule = parentRule;
  }

  public Rule getParentRule() {
    return parentRule;
  }

  public Rule getRule() {
    return this;
  }

  public String toEBNFNotation() {
    return name + " := " + matcher.toString();
  }

  public String toString() {
    return name;
  }
}
