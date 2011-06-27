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
import com.sonar.sslr.impl.events.ExtendedStackTrace.RuleWithPosition;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;

public class ExtendedStackTraceStream {
	
  private final static int STACK_TRACE_RULE_STARTING_WITH_TOKENS = 4;
  private final static int SOURCE_CODE_TOKENS_WINDOW = 30;
  private final static int LINE_AND_COLUMN_LEFT_PAD_LENGTH = 6;
  private final static int LAST_SUCCESSFUL_TOKENS_WINDOW = 7;
	
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
        } else previousLine = currentLine;

        /* Flush the empty lines (to avoid side effects in displayToken, where the column of the token is used for padding) */
        stream.print(lineBuilder.toString());
        lineBuilder = new StringBuilder();

        /* Start the new line */
        displaySourceCodeLineHeader(lineBuilder, token.getLine(), failedToken.getLine());
      }

      displayToken(lineBuilder, token);
    }

    if (tokens.size() > 0)
      stream.println(lineBuilder.toString());
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
    String line = (parsingErrorLine == currentLine) ? "-->" : Integer.toString(currentLine);

    for (int i = 0; i < LINE_AND_COLUMN_LEFT_PAD_LENGTH - line.length() - 1; i++) {
      lineBuilder.append(" ");
    }

    lineBuilder.append(line);
    lineBuilder.append(" ");
  }
  
  private static void displayToken(StringBuilder lineBuilder, Token token) {
    while (lineBuilder.length() - LINE_AND_COLUMN_LEFT_PAD_LENGTH < token.getColumn()) {
      lineBuilder.append(" ");
    }
    lineBuilder.append(token.getValue());
  }
  
  private static void displayStackTrace(ExtendedStackTrace extendedStackTrace, PrintStream stream) {
    if (extendedStackTrace.longestStack.size() == 0) {
      stream.println("[ Not a single match ]");
    } else {
      stream.println("on matcher " + MatcherTreePrinter.print(extendedStackTrace.longestOutertMatcherWithPosition.getMatcher()));
      stream.print(getPosition(extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getLine(), extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getColumn()));
      stream.print(MatcherTreePrinter.print(extendedStackTrace.longestMatcherWithPosition.getMatcher()) + " expected but ");
      stream.println("\"" + extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getValue().replace("\"", "\\\"") + "\"" + " [" + extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getType() + "] found");

      for (int i = extendedStackTrace.longestStack.size() - 1; i >= 0; i--) {
        stream.print("at ");
        displayStackTraceRuleWithPosition(extendedStackTrace, stream, extendedStackTrace.longestStack.get(i));
      }
    }
  }
  
  private static String getPosition(int line, int column) {
    return "  " + String.format("%1$#" + LINE_AND_COLUMN_LEFT_PAD_LENGTH + "s", line) + " : "
        + String.format("%1$#" + LINE_AND_COLUMN_LEFT_PAD_LENGTH + "s", column) + "  : ";
  }
  
  private static void displayStackTraceRuleWithPosition(ExtendedStackTrace extendedStackTrace, PrintStream stream, RuleWithPosition ruleWithPosition) {
    StringBuilder ruleBuilder = new StringBuilder();

    Token fromToken = extendedStackTrace.longestParsingState.readToken(ruleWithPosition.getFromIndex());

    ruleBuilder.append(ruleWithPosition.getRule().getName() + System.getProperty("line.separator"));
    ruleBuilder.append(getPosition(fromToken.getLine(), fromToken.getColumn()));

    /* Display the next SOURCE_SNIPPET_NEXT_TOKENS tokens, until the toIndex */
    int i;
    for (i = ruleWithPosition.getFromIndex(); i < ruleWithPosition.getFromIndex() + STACK_TRACE_RULE_STARTING_WITH_TOKENS && i < ruleWithPosition.getToIndex() && i < extendedStackTrace.longestParsingState.lexerSize; i++) {
      ruleBuilder.append(extendedStackTrace.longestParsingState.readToken(i).getValue());
      ruleBuilder.append(" ");
    }

    /* Display "..." if there are still more tokens available after the last one displayed before the toIndex */
    if (i < ruleWithPosition.getToIndex() && i < extendedStackTrace.longestParsingState.lexerSize) {
      ruleBuilder.append("...");
    }

    stream.println(ruleBuilder);
  }

  private static void displayLastSuccessfulTokens(ExtendedStackTrace extendedStackTrace, PrintStream stream) {
    /* Display the LAST_SUCCESSFUL_TOKENS_WINDOW last successfully consumed tokens, and the name of the rule which consumed them */
    for (int i = extendedStackTrace.longestIndex - 1; i >= extendedStackTrace.longestIndex - LAST_SUCCESSFUL_TOKENS_WINDOW && i >= 0; i--) {
      Token token = extendedStackTrace.longestParsingState.readToken(i);
      stream.println("  \"" + token.getValue().replace("\"", "\\\"") + "\" at " + token.getLine() + ":" + token.getColumn()
          + " consumed by " + getTokenConsumer(extendedStackTrace, i).getRule().getName());
    }
  }
  
  private static RuleWithPosition getTokenConsumer(ExtendedStackTrace extendedStackTrace, int lexerIndex) {
    for (RuleWithPosition ruleWithPosition : extendedStackTrace.longestStack) {
      if (ruleWithPosition.getFromIndex() <= lexerIndex && lexerIndex < ruleWithPosition.getToIndex()) {
        return ruleWithPosition;
      }
    }

    return null;
  }
  
}
