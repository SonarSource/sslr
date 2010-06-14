/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;
import com.sonar.sslr.impl.matcher.TokenTypeMatcher;

public class ParsingStackTrace {

  private final StringBuilder stackTrace = new StringBuilder();
  private static final int STACK_TRACE_DEPTH = 8;
  private final ParsingState parsingState;

  private ParsingStackTrace(ParsingState parsingState) {
    this.parsingState = parsingState;
    displayExpectedToken(parsingState.getOutpostMatcher());
    displayButWasToken(parsingState.getOutpostMatcherToken());
    displayLastParentRules((RuleImpl) parsingState.getOutpostMatcher().getRule(), STACK_TRACE_DEPTH);
  }

  private void displayExpectedToken(Matcher matcher) {
    stackTrace.append("Expected : <");
    stackTrace.append(matcher.toString());
    if (matcher instanceof TokenTypeMatcher) {
      stackTrace.append(" type");
    }
    stackTrace.append(">");
  }

  private void displayButWasToken(Token token) {

    stackTrace.append(" but was : <");
    if (token != null) {
      stackTrace.append(token.getValue());
      stackTrace.append(" [" + token.getType() + "]>");
      stackTrace.append(" (");
      if (token.isCopyBook()) {
        stackTrace.append("copy book ");
      }
      stackTrace.append("'" + token.getFile().getName() + "':");
      stackTrace.append(" Line " + token.getLine() + " /");
      stackTrace.append(" Column " + token.getColumn());
      if (token.isCopyBook()) {
        stackTrace.append(" called from file ");
        stackTrace.append("'" + token.getCopyBookOriginalFileName() + "':");
        stackTrace.append(" Line " + token.getCopyBookOriginalLine());
      }
      stackTrace.append(")");
    } else {
      stackTrace.append("EOF>");
      if (parsingState.lexerSize > 0) {
        Token lastToken = parsingState.peekToken(parsingState.lexerSize - 1, null);
        stackTrace.append(" ('" + lastToken.getFile().getName() + "')");
      }
    }
    stackTrace.append("\n");
  }

  private void displayLastParentRules(RuleImpl ruleImpl, int level) {
    if (level == 0 || ruleImpl == null) {
      return;
    }
    stackTrace.append("  at ");
    stackTrace.append(ruleImpl.toEBNFNotation());
    stackTrace.append("\n");
    displayLastParentRules(ruleImpl.getParentRule(), level - 1);
  }

  public static String generate(ParsingState state) {
    ParsingStackTrace stackTrace = new ParsingStackTrace(state);
    return stackTrace.toString();
  }

  public String toString() {
    return stackTrace.toString();
  }

}
