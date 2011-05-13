/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class ExtendedStackTrace implements ParsingEventListener {
	private final static int SOURCE_CODE_TOKENS_WINDOW = 30;
	private final static int SOURCE_CODE_LINE_HEADER_WIDTH = 6;
	
	private Stack<Matcher> currentStack = new Stack<Matcher>();
	private Stack<RuleImpl> longestStack = new Stack<RuleImpl>();
	private int longestIndex = -1;
	private Matcher longestMatcher;
	private ParsingState longestParsingState;
	
	public void enterRule(RuleImpl rule, ParsingState parsingState) {
		currentStack.push(rule);
	}

	public void exitWithMatchRule(RuleImpl rule, ParsingState parsingState, AstNode astNode) {
		currentStack.pop();
	}

	public void exitWithoutMatchRule(RuleImpl rule, ParsingState parsingState, RecognitionExceptionImpl re) {
		currentStack.pop();
	}

	public void enterMatcher(Matcher matcher, ParsingState parsingState) {
		currentStack.push(matcher);
	}

	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
		if (parsingState.lexerIndex > longestIndex) {
			/* New longest path! */
			longestIndex = parsingState.lexerIndex;
			
			longestStack = new Stack<RuleImpl>();
			for (Matcher currentMatcher: currentStack) {
				if (currentMatcher instanceof RuleImpl) longestStack.push((RuleImpl)currentMatcher);
			}
			
			longestMatcher = matcher;
			this.longestParsingState = parsingState;
		}
		currentStack.pop();
	}

	public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, RecognitionExceptionImpl re) {
		currentStack.pop();
	}
	
	public void printExtendedStackTrace() {
		System.out.println("Stack trace:");
		System.out.println("------------");
		
		if (longestStack.size() == 0) {
			System.out.println("Not a single match.");
		}
		else {
			RuleImpl rule = longestStack.pop();
			System.out.println(rule.getName());
			
			while (longestStack.size() > 0) {
				System.out.println("  at " + longestStack.pop().getName());
			}
		}
		
		System.out.println("Source code:");
		System.out.println("------------");
		displaySourceCode();
		
		System.out.println("End of stack trace");
	}
	
  private void displaySourceCode() {
  	System.out.println("longest line = " +  longestParsingState.readToken(longestIndex + 1).getLine());
  	
    List<Token> tokens = getTokensToDisplayAroundOutpostMatcherToken();
    int previousLine = 1;
    StringBuilder lineBuilder = new StringBuilder();
    for (Token token : tokens) {
      int currentLine = token.getLine();
      if (currentLine != previousLine) {
      	System.out.println(lineBuilder.toString() + "\n");
        lineBuilder = new StringBuilder();
        previousLine = currentLine;
        displaySourceCodeLineHeader(lineBuilder, token, longestParsingState.readToken(longestIndex + 1).getLine());
      }
      displayToken(lineBuilder, token);
    }
    System.out.println(lineBuilder.toString() + "\n");
    System.out.println("------\n");
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
    int outpostMatcherTokenIndex = longestIndex + 1;
    for (int i = outpostMatcherTokenIndex - SOURCE_CODE_TOKENS_WINDOW; i <= outpostMatcherTokenIndex + SOURCE_CODE_TOKENS_WINDOW; i++) {
      if (i < 0 || i > longestParsingState.lexerSize - 1) {
        continue;
      }
      tokens.add(longestParsingState.readToken(i));
    }
    return tokens;
  }
	
}
