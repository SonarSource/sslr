/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import com.sonarsource.sslr.ParsingState;
import com.sonarsource.sslr.RecognitionExceptionImpl;
import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.api.AstNodeType;
import com.sonarsource.sslr.api.TokenType;

public abstract class Matcher implements AstNodeType {

  protected RuleImpl parentRule;

  public abstract void setParentRule(RuleImpl parentRule);

  public RuleImpl getRule() {
    return parentRule;
  }

  public boolean isMatching(ParsingState parsingState) {
    int indexBeforeStarting = parsingState.lexerIndex;
    try {
      match(parsingState);
      return true;
    } catch (RecognitionExceptionImpl e) {
      return false;
    } finally {
      parsingState.lexerIndex = indexBeforeStarting;
    }
  }

  public boolean hasToBeSkippedFromAst() {
    return true;
  }

  protected abstract AstNode match(ParsingState parsingState);

  public final AstNode parse(ParsingState parsingState) {
    try {
      return this.match(parsingState);
    } catch (RecognitionExceptionImpl e) {
      throw new RecognitionExceptionImpl(parsingState);
    }
  }

  protected static final Matcher[] convertToMatchers(Object[] objects) {
    Matcher[] matchers = new Matcher[objects.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = convertToMatcher(objects[i]);
    }
    return matchers;
  }

  protected static final Matcher convertToMatcher(Object object) {
    Matcher matcher;
    if (object instanceof String) {
      matcher = new TokenValueMatcher((String) object);
    } else if (object instanceof TokenType) {
      TokenType tokenType = (TokenType) object;
      matcher = new TokenTypeMatcher(tokenType, tokenType.hasToBeSkippedFromAst());
    } else {
      try {
        matcher = (Matcher) object;
      } catch (ClassCastException e) {
        throw new IllegalStateException("The matcher object can't be anything else than a Matcher, String or TokenType. Object = " + object);
      }
    }
    if (matcher instanceof RuleImpl) {
      return new ProxyMatcher(matcher);
    } else {
      return matcher;
    }
  }
}
