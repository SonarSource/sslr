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
	private final static int STACK_TRACE_RULE_WINDOW = 3;
	private final static int SOURCE_CODE_TOKENS_WINDOW = 30;
	private final static int SOURCE_CODE_LINE_HEADER_WIDTH = 6;
	
	private Stack<MatcherWithPosition> currentStack = new Stack<MatcherWithPosition>();
	private Stack<RuleWithPosition> longestStack = new Stack<RuleWithPosition>();
	private int longestIndex = -1;
	private ParsingState longestParsingState;
	
	private class MatcherWithPosition {
		
		private Matcher matcher;
		private int fromIndex;
		
		public MatcherWithPosition(Matcher matcher, int fromIndex) {
			this.matcher = matcher;
			this.fromIndex = fromIndex;
		}
		
		public Matcher getMatcher() {
			return matcher;
		}
		
		public int getFromIndex() {
			return fromIndex;
		}
		
	}
	
	private class RuleWithPosition extends MatcherWithPosition {
		
		private int toIndex;
		
		public RuleWithPosition(RuleImpl rule, int fromIndex) {
			super(rule, fromIndex);
			this.toIndex = -1;
		}
		
		public RuleImpl getRule() {
			return (RuleImpl)getMatcher();
		}
		
		public void setToIndex(int toIndex) {
			this.toIndex = toIndex;
		}
		
		public int getToIndex() {
			return toIndex;
		}
		
	}
	
	public void enterRule(RuleImpl rule, ParsingState parsingState) {
		/* The beginning of a rule is the end of the last one, so update the last's one toIndex */
		for (MatcherWithPosition currentMatcherWithPosition: currentStack) {
			if (currentMatcherWithPosition instanceof RuleWithPosition) {
				RuleWithPosition ruleWithCurrentPosition = (RuleWithPosition)currentMatcherWithPosition;
				ruleWithCurrentPosition.setToIndex(parsingState.lexerIndex);
				break;
			}
		}
		
		currentStack.push(new RuleWithPosition(rule, parsingState.lexerIndex));
	}

	public void exitWithMatchRule(RuleImpl rule, ParsingState parsingState, AstNode astNode) {
		currentStack.pop();
	}

	public void exitWithoutMatchRule(RuleImpl rule, ParsingState parsingState, RecognitionExceptionImpl re) {
		currentStack.pop();
	}

	public void enterMatcher(Matcher matcher, ParsingState parsingState) {
		currentStack.push(new MatcherWithPosition(matcher, parsingState.lexerIndex));
	}

	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
		/* Update the toIndex */
		MatcherWithPosition matcherWithPosition = currentStack.pop();
		currentStack.push(matcherWithPosition);
		
		/* Handle the longest path */
		if (parsingState.lexerIndex > longestIndex) {
			/* New longest path! */
			longestIndex = parsingState.lexerIndex;
			
			longestStack = new Stack<RuleWithPosition>();
			for (MatcherWithPosition currentMatcherWithPosition: currentStack) {
				Matcher currentMatcher = currentMatcherWithPosition.getMatcher();
				if (currentMatcher instanceof RuleImpl) longestStack.push(new RuleWithPosition((RuleImpl)currentMatcher, currentMatcherWithPosition.getFromIndex()));
			}
			
			this.longestParsingState = parsingState;
		}
		currentStack.pop();
	}

	public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, RecognitionExceptionImpl re) {
		currentStack.pop();
	}
	
	public void printExtendedStackTrace() {
		System.out.println("Source code:");
		System.out.println("------------");
		displaySourceCode();
		System.out.println("------------");
		
		System.out.println("Stack trace:");
		System.out.println("------------");
		displayStackTrace();
		System.out.println("------------");
	}
	
	private void displayStackTrace() {
		if (longestStack.size() == 0) {
			System.out.println("Not a single match.");
		}
		else {
			displayStackTraceRuleWithPosition(longestStack.pop());
			
			while (longestStack.size() > 0) {
				System.out.print("  at ");
				displayStackTraceRuleWithPosition(longestStack.pop());
			}
		}
	}
	
	private void displayStackTraceRuleWithPosition(RuleWithPosition ruleWithPosition) {
		StringBuilder ruleBuilder = new StringBuilder();
		
		Token fromToken = longestParsingState.readToken(ruleWithPosition.getFromIndex());
		
		ruleBuilder.append(ruleWithPosition.getRule().getName());
		ruleBuilder.append(" from " + fromToken.getLine() + ":" + fromToken.getColumn() + ", starting with: ");
		
		/* Display the next SOURCE_SNIPPET_NEXT_TOKENS tokens */
		int i;
		for (i = ruleWithPosition.getFromIndex(); i < ruleWithPosition.getFromIndex() + STACK_TRACE_RULE_WINDOW && i <= longestParsingState.lexerIndex + 1; i++) {
			ruleBuilder.append(longestParsingState.readToken(i).getValue());
			ruleBuilder.append(" ");
		}

		/* Display "..." if there are still more tokens available after the last one displayed */
		if (i <= longestParsingState.lexerIndex + 1) ruleBuilder.append("...");

		System.out.println(ruleBuilder);
	}
	
  private void displaySourceCode() {
  	Token failedToken = longestParsingState.readToken(longestIndex);

    List<Token> tokens = getTokensToDisplayAroundOutpostMatcherToken();
    int previousLine = 0;
    StringBuilder lineBuilder = new StringBuilder();
    for (Token token : tokens) {
      int currentLine = token.getLine();
      if (currentLine != previousLine) {
      	/* Flush the previous line */
      	if (previousLine != 0) {
      		System.out.println(lineBuilder.toString());
      	}
      	
      	/* Prepare the new one */
        lineBuilder = new StringBuilder();
        previousLine++;
      	
        /* Handle the potential empty lines between the previous and current token */
        while (previousLine < currentLine) {
        	displaySourceCodeLineHeader(lineBuilder, previousLine, failedToken.getLine());
        	lineBuilder.append("\n");
        	previousLine++;
        }
        
        /* Flush the empty lines (to avoid side effects in displayToken, where the column of the token is used for padding) */
        System.out.print(lineBuilder.toString());
        lineBuilder = new StringBuilder();
        
      	/* Start the new line */
        displaySourceCodeLineHeader(lineBuilder, token.getLine(), failedToken.getLine());
      }
      
      displayToken(lineBuilder, token);
    }
    
    if (tokens.size() > 0) System.out.println(lineBuilder.toString());
  }
  
  private void displayToken(StringBuilder lineBuilder, Token token) {
    while (lineBuilder.length() - SOURCE_CODE_LINE_HEADER_WIDTH < token.getColumn()) {
      lineBuilder.append(" ");
    }
    lineBuilder.append(token.getValue());
  }
  
  private void displaySourceCodeLineHeader(StringBuilder lineBuilder, int currentLine, int parsingErrorLine) {
    String line = (parsingErrorLine == currentLine) ? "-->" : Integer.toString(currentLine);
    
    for (int i = 0; i < SOURCE_CODE_LINE_HEADER_WIDTH - line.length() - 1; i++) {
      lineBuilder.append(" ");
    }
    
    lineBuilder.append(line);
    lineBuilder.append(" ");
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
