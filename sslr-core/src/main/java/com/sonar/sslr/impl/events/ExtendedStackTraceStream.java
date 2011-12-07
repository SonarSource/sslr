/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public final class ExtendedStackTraceStream {

  private static final int STACK_TRACE_RULE_STARTING_WITH_TOKENS = 4;
  private static final int SOURCE_CODE_TOKENS_WINDOW = 30;
  private static final int LINE_AND_COLUMN_LEFT_PAD_LENGTH = 6;
  private static final int LAST_SUCCESSFUL_TOKENS_WINDOW = 7;

  private ExtendedStackTraceStream() {
  }

  public static void print(ExtendedStackTrace extendedStackTrace, PrintStream stream) {
    stream.println("Source Snippet:");
    stream.println("---------------");
    // TODO: Integrate the error pointer directly into the source snippet
    displaySourceSnippet(extendedStackTrace, stream);
    stream.println("---------------");

    stream.println();
    displayStackTrace(extendedStackTrace, stream);

    stream.println();
    stream.println("Last successful tokens:");
    stream.println("-----------------------");
    displayLastSuccessfulTokens(extendedStackTrace, stream);
  }

  private static void displaySourceSnippet(ExtendedStackTrace extendedStackTrace, PrintStream stream) {
    Token failedToken = extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex);

    List<Token> tokens = getTokensToDisplayAroundOutpostMatcherToken(extendedStackTrace);
    int previousLine = 0;
    StringBuilder lineBuilder = new StringBuilder();
    for (Token token : tokens) {
      int currentLine = token.getLine();
      if (currentLine != previousLine) {
        /* Flush the previous line */
        if (previousLine != 0) {
          stream.println(lineBuilder.toString());
        }

        /* Prepare the new one */
        lineBuilder = new StringBuilder();
        previousLine++;

        /* Handle the potential empty lines between the previous and current token */
        if (previousLine != 1) {
          while (previousLine < currentLine) {
            displaySourceCodeLineHeader(lineBuilder, previousLine, failedToken.getLine());
            lineBuilder.append(System.getProperty("line.separator"));
            previousLine++;
          }
        } else {
          previousLine = currentLine;
        }

        /* Flush the empty lines (to avoid side effects in displayToken, where the column of the token is used for padding) */
        stream.print(lineBuilder.toString());
        lineBuilder = new StringBuilder();

        /* Start the new line */
        displaySourceCodeLineHeader(lineBuilder, token.getLine(), failedToken.getLine());
      }

      previousLine = displayToken(lineBuilder, token, failedToken.getLine());
    }

    if (tokens.size() > 0) {
      stream.println(lineBuilder.toString());
    }
  }

  private static List<Token> getTokensToDisplayAroundOutpostMatcherToken(ExtendedStackTrace extendedStackTrace) {
    List<Token> tokens = new ArrayList<Token>();
    int outpostMatcherTokenIndex = extendedStackTrace.longestIndex + 1;
    for (int i = outpostMatcherTokenIndex - SOURCE_CODE_TOKENS_WINDOW; i <= outpostMatcherTokenIndex + SOURCE_CODE_TOKENS_WINDOW; i++) {
      if (i < 0 || i > extendedStackTrace.longestParsingState.lexerSize - 1) {
        continue;
      }
      tokens.add(extendedStackTrace.longestParsingState.readToken(i));
    }
    return tokens;

  }

  private static void displaySourceCodeLineHeader(StringBuilder lineBuilder, int currentLine, int parsingErrorLine) {
    String line = parsingErrorLine == currentLine ? "-->" : Integer.toString(currentLine);

    for (int i = 0; i < LINE_AND_COLUMN_LEFT_PAD_LENGTH - line.length() - 1; i++) {
      lineBuilder.append(" ");
    }

    lineBuilder.append(line);
    lineBuilder.append(" ");
  }

  private static int displayToken(StringBuilder lineBuilder, Token token, int parsingErrorLine) {
    int currentLine = token.getLine();
    String[] tokenLines = token.getValue().replace("\r", "").split("\n", -1);

    /* Display the first line */
    while (lineBuilder.length() - LINE_AND_COLUMN_LEFT_PAD_LENGTH < token.getColumn()) {
      lineBuilder.append(" ");
    }
    lineBuilder.append(tokenLines[0]);

    /* Display the following lines */
    for (int i = 1; i < tokenLines.length; i++) {
      currentLine++;
      lineBuilder.append(System.getProperty("line.separator"));
      displaySourceCodeLineHeader(lineBuilder, currentLine, parsingErrorLine);

      lineBuilder.append(tokenLines[i]);
    }

    return currentLine;
  }

  private static void displayStackTrace(ExtendedStackTrace extendedStackTrace, PrintStream stream) {
    if (extendedStackTrace.longestStack.size() == 0) {
      stream.println("[ Not a single match ]");
    } else {
      stream.println("on matcher " + MatcherTreePrinter.print(extendedStackTrace.longestOutertMatcher));
      stream.print(getPosition(extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getLine(),
          extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getColumn()));
      stream.print(MatcherTreePrinter.print(extendedStackTrace.longestMatcher) + " expected but ");
      stream.println("\""
          + extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getValue().replace("\"", "\\\"") + "\""
          + " [" + extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getType() + "] found");

      for (int i = extendedStackTrace.longestStack.size() - 1; i >= 0; i--) {
        stream.print("at ");
        displayStackTraceRuleWithPosition(extendedStackTrace, stream, i);
      }
    }
  }

  private static String getPosition(int line, int column) {
    return "  " + String.format("%" + LINE_AND_COLUMN_LEFT_PAD_LENGTH + "d", line) + " : "
        + String.format("%" + LINE_AND_COLUMN_LEFT_PAD_LENGTH + "d", column) + "  : ";
  }

  private static void displayStackTraceRuleWithPosition(ExtendedStackTrace extendedStackTrace, PrintStream stream, int ruleWithPosition) {
    StringBuilder ruleBuilder = new StringBuilder();

    Token fromToken = extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestStack.getFromIndex(ruleWithPosition));

    ruleBuilder.append(extendedStackTrace.longestStack.getRule(ruleWithPosition).getName() + System.getProperty("line.separator"));
    ruleBuilder.append(getPosition(fromToken.getLine(), fromToken.getColumn()));

    /* Display the next SOURCE_SNIPPET_NEXT_TOKENS tokens, until the toIndex */
    int i;
    for (i = extendedStackTrace.longestStack.getFromIndex(ruleWithPosition); i < extendedStackTrace.longestStack
        .getFromIndex(ruleWithPosition) + STACK_TRACE_RULE_STARTING_WITH_TOKENS
        && i < extendedStackTrace.longestStack.getToIndex(ruleWithPosition) && i < extendedStackTrace.longestParsingState.lexerSize; i++) {
      ruleBuilder.append(extendedStackTrace.longestParsingState.readToken(i).getValue());
      ruleBuilder.append(" ");
    }

    /* Display "..." if there are still more tokens available after the last one displayed before the toIndex */
    if (i < extendedStackTrace.longestStack.getToIndex(ruleWithPosition) && i < extendedStackTrace.longestParsingState.lexerSize) {
      ruleBuilder.append("...");
    }

    stream.println(ruleBuilder);
  }

  private static void displayLastSuccessfulTokens(ExtendedStackTrace extendedStackTrace, PrintStream stream) {
    /* Display the LAST_SUCCESSFUL_TOKENS_WINDOW last successfully consumed tokens, and the name of the rule which consumed them */
    for (int i = extendedStackTrace.longestIndex - 1; i >= extendedStackTrace.longestIndex - LAST_SUCCESSFUL_TOKENS_WINDOW && i >= 0; i--) {
      Token token = extendedStackTrace.longestParsingState.readToken(i);
      stream.println("  \"" + token.getValue().replace("\"", "\\\"") + "\" at " + token.getLine() + ":" + token.getColumn()
          + " consumed by " + getTokenConsumer(extendedStackTrace, i).getName());
    }
  }

  private static RuleMatcher getTokenConsumer(ExtendedStackTrace extendedStackTrace, int lexerIndex) {
    for (int i = 0; i < extendedStackTrace.longestStack.size(); i++) {
      if (extendedStackTrace.longestStack.getFromIndex(i) <= lexerIndex && lexerIndex < extendedStackTrace.longestStack.getToIndex(i)) {
        return extendedStackTrace.longestStack.getRule(i);
      }
    }

    throw new IllegalStateException("No token consumer was found for lexerIndex = " + lexerIndex);
  }

}
