/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import com.sonarsource.sslr.ParsingState;
import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.api.Rule;

public class RuleImpl extends Matcher implements Rule {

  protected String name;
  protected Matcher matcher;
  private boolean hasSeveralParents = false;
  protected boolean hasToBeSkippedWhenBuildingAst = false;

  public RuleImpl(String name) {
    this.name = name;
  }

  public AstNode match(ParsingState parsingState) {
    int startIndex = parsingState.lexerIndex;
    AstNode childNode = matcher.match(parsingState);

    AstNode astNode = new AstNode(this, name, parsingState.peekTokenIfExists(startIndex, matcher), hasToBeSkippedWhenBuildingAst);
    astNode.addChild(childNode);
    return astNode;
  }

  public boolean hasToBeSkippedFromAst() {
    return false;
  }

  public RuleImpl is(Object... matchers) {
    if (matchers.length == 0) {
      throw new IllegalStateException("The rule '" + name + "' should at least contains one matcher.");
    }
    setMatcher(Matchers.and(matchers));
    return this;
  }

  public void mock() {
    setMatcher(new TokenValueMatcher(name.toUpperCase()));
  }

  public RuleImpl or(Object... matchers) {
    if (matchers.length == 0) {
      throw new IllegalStateException("A rule should at least contains one matcher.");
    }
    setMatcher(Matchers.or(matchers));
    return this;
  }

  public RuleImpl skip() {
    hasToBeSkippedWhenBuildingAst = true;
    return this;
  }

  protected void setMatcher(Matcher matcher) {
    this.matcher = matcher;
    matcher.setParentRule(this);
  }

  public void setParentRule(RuleImpl parentRule) {
    if (this.parentRule != null && parentRule != this.parentRule) {
      hasSeveralParents = true;
    }
    if (hasSeveralParents) {
      this.parentRule = null;
      return;
    }
    this.parentRule = parentRule;
  }

  public RuleImpl getParentRule() {
    return parentRule;
  }

  public RuleImpl getRule() {
    return this;
  }

  public String toEBNFNotation() {
    return name + " := " + matcher.toString();
  }

  public String toString() {
    return name;
  }
}
