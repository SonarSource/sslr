/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.RecognitionException;
import com.sonarsource.parser.ast.AstNode;
import com.sonarsource.sslr.api.AstNodeType;
import com.sonarsource.sslr.api.TokenType;

public abstract class Matcher implements AstNodeType {

  protected Rule parentRule;

  public abstract void setParentRule(Rule parentRule);

  public Rule getRule() {
    return parentRule;
  }

  public boolean isMatching(ParsingState parsingState) {
    int indexBeforeStarting = parsingState.lexerIndex;
    try {
      match(parsingState);
      return true;
    } catch (RecognitionException e) {
      return false;
    } finally {
      parsingState.lexerIndex = indexBeforeStarting;
    }
  }

  protected abstract AstNode match(ParsingState parsingState);

  public final AstNode parse(ParsingState parsingState) {
    try {
      return this.match(parsingState);
    } catch (RecognitionException e) {
      throw new RecognitionException(parsingState);
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
    if (matcher instanceof Rule) {
      return new ProxyMatcher(matcher);
    } else {
      return matcher;
    }
  }
}
