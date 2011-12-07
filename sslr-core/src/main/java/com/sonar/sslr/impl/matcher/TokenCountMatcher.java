/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.Arrays;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;

public class TokenCountMatcher extends MemoizedMatcher {

  private final Operator operator;
  private final int n;

  public enum Operator {
    EQUAL, LESS_THAN, GREATER_THAN
  }

  protected TokenCountMatcher(Operator operator, int n, Matcher matcher) {
    super(matcher);

    this.operator = operator;
    this.n = n;
  }

  @Override
  protected final AstNode matchWorker(ParsingState parsingState) {
    int startIndex = parsingState.lexerIndex;
    AstNode astNode = super.children[0].match(parsingState);
    int stopIndex = parsingState.lexerIndex;

    int consumedTokens = stopIndex - startIndex;

    switch (operator) {
      case EQUAL:
        if (consumedTokens != n) {
          throw BacktrackingEvent.create();
        }
        break;
      case LESS_THAN:
        if (consumedTokens >= n) {
          throw BacktrackingEvent.create();
        }
        break;
      case GREATER_THAN:
        if (consumedTokens <= n) {
          throw BacktrackingEvent.create();
        }
        break;
      default:
        throw BacktrackingEvent.create();
    }

    return astNode;
  }

  @Override
  public String toString() {
    return "tokenCount(TokenCountMatcher.Operator." + operator + ", " + n + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getClass().hashCode();
    result = prime * result + Arrays.hashCode(children);
    result = prime * result + n;
    result = prime * result + (operator == null ? 0 : operator.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TokenCountMatcher other = (TokenCountMatcher) obj;
    if ( !Arrays.equals(children, other.children)) {
      return false;
    }
    if (n != other.n) {
      return false;
    }
    if (operator != other.operator) {
      return false;
    }
    return true;
  }

}
