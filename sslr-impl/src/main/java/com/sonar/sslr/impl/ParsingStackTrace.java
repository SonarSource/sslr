/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import java.util.ArrayList;
import java.util.List;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;
import com.sonar.sslr.impl.matcher.TokenTypeMatcher;

public class ParsingStackTrace {

  private final StringBuilder stackTrace = new StringBuilder();
  private static final int STACK_TRACE_DEPTH = 8;
  private final ParsingState parsingState;
  private final static int SOURCE_CODE_TOKENS_WINDOW = 30;
  private final static int SOURCE_CODE_LINE_HEADER_WIDTH = 6;

  private ParsingStackTrace(ParsingState parsingState, boolean displaySourceCode) {
    this.parsingState = parsingState;
    if (displaySourceCode) {
      displaySourceCode();
    }
    displayExpectedToken(parsingState.getOutpostMatcher());
    displayButWasToken(parsingState.getOutpostMatcherToken());
    displayLastParentRules((RuleImpl) parsingState.getOutpostMatcher().getRule(), STACK_TRACE_DEPTH);
  }

  private void displaySourceCode() {
    List<Token> tokens = getTokensToDisplayAroundOutpostMatcherToken();
    stackTrace.append("------");
    int previousLine = -1;
    StringBuilder lineBuilder = new StringBuilder();
    for (Token token : tokens) {
      int currentLine = token.getLine();
      if (currentLine != previousLine) {
        stackTrace.append(lineBuilder.toString() + "\n");
        lineBuilder = new StringBuilder();
        previousLine = currentLine;
        displaySourceCodeLineHeader(lineBuilder, token, parsingState.getOutpostMatcherTokenLine());
      }
      displayToken(lineBuilder, token);
    }
    stackTrace.append(lineBuilder.toString() + "\n");
    stackTrace.append("------\n");
  }

  private void displayToken(StringBuilder lineBuilder, Token token) {
    while (lineBuilder.length() - SOURCE_CODE_LINE_HEADER_WIDTH < token.getColumn()) {
      lineBuilder.append(" ");
    }
    lineBuilder.append(token.getValue());
  }

  private void displaySourceCodeLineHeader(StringBuilder lineBuilder, Token firstTokenInLine, int parsingErrorLine) {
    if (parsingErrorLine != firstTokenInLine.getLine()) {
      String line = Integer.toString(firstTokenInLine.getLine());
      for (int i = 0; i < SOURCE_CODE_LINE_HEADER_WIDTH - line.length() - 1; i++) {
        lineBuilder.append(" ");
      }
      lineBuilder.append(line);
      lineBuilder.append(" ");
    } else {
      lineBuilder.append("-->   ");
    }
  }

  private List<Token> getTokensToDisplayAroundOutpostMatcherToken() {
    List<Token> tokens = new ArrayList<Token>();
    int outpostMatcherTokenIndex = parsingState.getOutpostMatcherTokenIndex();
    int outpostMatcherTokenLine = parsingState.getOutpostMatcherTokenLine();
    for (int i = outpostMatcherTokenIndex - SOURCE_CODE_TOKENS_WINDOW; i <= outpostMatcherTokenIndex + SOURCE_CODE_TOKENS_WINDOW; i++) {
      if (i < 0 || i > parsingState.lexerSize - 1) {
        continue;
      }
      Token token = parsingState.readToken(i);
      tokens.add(parsingState.readToken(i));
    }
    return tokens;
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
    ParsingStackTrace stackTrace = new ParsingStackTrace(state, false);
    return stackTrace.toString();
  }

  public String toString() {
    return stackTrace.toString();
  }

  public static String generateFullStackTrace(ParsingState state) {
    if (state.getOutpostMatcher() == null) {
      return "";
    }
    ParsingStackTrace stackTrace = new ParsingStackTrace(state, true);
    return stackTrace.toString();
  }

}
