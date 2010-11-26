/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstListener;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.NeverSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;

public class RuleImpl extends Matcher implements Rule {

  protected String name;
  protected Matcher matcher;
  private AstListener listener;
  private AstNodeType astNodeType = new NeverSkipFromAst();
  private Token lastToken = null;

  public RuleImpl(String name) {
    this.name = name;
  }

  public AstNode match(ParsingState parsingState) {
    int startIndex = parsingState.lexerIndex;
    if (matcher == null) {
      throw new IllegalStateException("The rule '" + name + "' hasn't beed defined.");
    }
    Token nextToken = parsingState.readToken(parsingState.lexerIndex);
    if(lastToken == nextToken){ //left recursion must be stopped
      throw RecognitionExceptionImpl.create();
    }
    lastToken = nextToken;
    parsingState.pushToParsingStack(this);
    AstNode childNode = matcher.match(parsingState);
    parsingState.popFromParsingStack();

    AstNode astNode = new AstNode(this, name, parsingState.peekTokenIfExists(startIndex, matcher));
    astNode.setAstNodeListener(listener);
    astNode.addChild(childNode);
    return astNode;
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    if (AstNodeSkippingPolicy.class.isAssignableFrom(astNodeType.getClass())) {
      return ((AstNodeSkippingPolicy) astNodeType).hasToBeSkippedFromAst(node);
    }
    return false;
  }

  public RuleImpl is(Object... matchers) {
    checkIfThereIsAtLeastOneMatcher(matchers);
    setMatcher(Matchers.and(matchers));
    return this;
  }

  private void checkIfThereIsAtLeastOneMatcher(Object[] matchers) {
    if (matchers.length == 0) {
      throw new IllegalStateException("The rule '" + name + "' should at least contains one matcher.");
    }
  }

  public void mockUpperCase() {
    setMatcher(new TokenValueMatcher(name.toUpperCase()));
  }

  public void mock() {
    setMatcher(new TokenValueMatcher(name));
  }

  public RuleImpl isOr(Object... matchers) {
    checkIfThereIsAtLeastOneMatcher(matchers);
    setMatcher(Matchers.or(matchers));
    return this;
  }

  public RuleImpl or(Object... matchers) {
    checkIfThereIsAtLeastOneMatcher(matchers);
    if (matcher == null) {
      throw new IllegalStateException("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    }
    setMatcher(Matchers.or(matcher, Matchers.and(matchers)));
    return this;
  }

  public RuleImpl and(Object... matchers) {
    checkIfThereIsAtLeastOneMatcher(matchers);
    if (matcher == null) {
      throw new IllegalStateException("The Rule.and(...) can't be called if the method Rule.is(...) hasn't been called first.");
    }
    setMatcher(Matchers.and(matcher, Matchers.and(matchers)));
    return this;
  }

  public RuleImpl orBefore(Object... matchers) {
    checkIfThereIsAtLeastOneMatcher(matchers);
    if (matcher == null) {
      throw new IllegalStateException("The Rule.or(...) can't be called if the method Rule.is(...) hasn't been called first.");
    }
    setMatcher(Matchers.or(Matchers.and(matchers), matcher));
    return this;
  }

  public RuleImpl skip() {
    astNodeType = new AlwaysSkipFromAst();
    return this;
  }

  protected void setMatcher(Matcher matcher) {
    this.matcher = matcher;
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

  public Rule setListener(AstListener listener) {
    this.listener = listener;
    return this;
  }

  public Rule skipIf(AstNodeType astNodeSkipPolicy) {
    this.astNodeType = astNodeSkipPolicy;
    return this;
  }

  public Rule skipIfOneChild() {
    this.astNodeType = new SkipFromAstIfOnlyOneChild();
    return this;
  }
}
