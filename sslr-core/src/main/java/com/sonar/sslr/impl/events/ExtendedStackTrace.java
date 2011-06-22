/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.MemoizerMatcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class ExtendedStackTrace extends ParsingEventListener {
	private final static int STACK_TRACE_RULE_STARTING_WITH_TOKENS = 4;
	private final static int SOURCE_CODE_TOKENS_WINDOW = 30;
	private final static int LINE_AND_COLUMN_LEFT_PAD_LENGTH = 6;
	private final static int LAST_SUCCESSFUL_TOKENS_WINDOW = 7;
	
	private Stack<MatcherWithPosition> currentStack;
	private Stack<RuleWithPosition> longestStack;
	private MatcherWithPosition longesOutertMatcherWithPosition;
	private MatcherWithPosition longestMatcherWithPosition;
	private int longestIndex;
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
		
		public RuleWithPosition(RuleMatcher rule, int fromIndex) {
			super(rule, fromIndex);
			this.toIndex = -1;
		}
		
		public RuleWithPosition(RuleMatcher rule, int fromIndex, int toIndex) {
			super(rule, fromIndex);
			this.toIndex = toIndex;
		}
		
		public RuleMatcher getRule() {
			return (RuleMatcher)getMatcher();
		}
		
		public void setToIndex(int toIndex) {
			this.toIndex = toIndex;
		}
		
		public int getToIndex() {
			return toIndex;
		}
		
	}
	
	@Override
	public void beginParse() {
		currentStack = new Stack<MatcherWithPosition>();
		longestStack = new Stack<RuleWithPosition>();
		longestIndex = -1;
	}

	@Override
	public void enterRule(RuleMatcher rule, ParsingState parsingState) {
		/* The beginning of a rule is the "end" (when partitioning) of the last one, so update the last's one toIndex */
		RuleWithPosition lastRuleWithPosition = null;
		for (MatcherWithPosition currentMatcherWithPosition: currentStack) {
			if (currentMatcherWithPosition instanceof RuleWithPosition) {
				lastRuleWithPosition = (RuleWithPosition)currentMatcherWithPosition;
			}
		}
		if (lastRuleWithPosition != null) {
			lastRuleWithPosition.setToIndex(parsingState.lexerIndex);
		}

		/* Add the newly entered rule to the current stack */
		currentStack.push(new RuleWithPosition(rule, parsingState.lexerIndex));
	}

	@Override
	public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
		currentStack.pop();
	}

	@Override
	public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState, BacktrackingException re) {
		currentStack.pop();
	}

	@Override
	public void enterMatcher(Matcher matcher, ParsingState parsingState) {
		currentStack.push(new MatcherWithPosition(matcher, parsingState.lexerIndex));
	}

	@Override
	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {	
		currentStack.pop();
	}

	@Override
	public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, BacktrackingException re) {
		/* Handle the longest path */
		if (parsingState.lexerIndex > longestIndex) {
			/* New longest path! */
			longestIndex = (parsingState.lexerIndex == parsingState.lexerSize) ? parsingState.lexerIndex - 1 : parsingState.lexerIndex; /* Handle the case in which EOF is successfully consumed by the grammar, but more is expected */			
			longestMatcherWithPosition = currentStack.peek();
			
			/* Set the longest matcher to the outer most one starting at the current index (but ignorning MemoizerMatchers) */
			for (MatcherWithPosition currentMatcherWithPosition: currentStack) {
				if (currentMatcherWithPosition instanceof MatcherWithPosition && currentMatcherWithPosition.getFromIndex() == parsingState.lexerIndex && !(currentMatcherWithPosition.getMatcher() instanceof MemoizerMatcher)) {
					longesOutertMatcherWithPosition = currentMatcherWithPosition;
					break;
				}
			}
			
			/* Set the current's rule toIndex */
			RuleWithPosition lastRuleWithPosition = null;
			for (MatcherWithPosition currentMatcherWithPosition: currentStack) {
				if (currentMatcherWithPosition instanceof RuleWithPosition) {
					lastRuleWithPosition = (RuleWithPosition)currentMatcherWithPosition;
				}
			}
			if (lastRuleWithPosition != null) {
				lastRuleWithPosition.setToIndex(parsingState.lexerIndex);
			}
			
			/* Copy the current stack to the longest stack (deep-copy / clone) */
			longestStack = new Stack<RuleWithPosition>();
			for (MatcherWithPosition currentMatcherWithPosition: currentStack) {
				if (currentMatcherWithPosition instanceof RuleWithPosition) {
					RuleWithPosition currentRuleWithPosition = (RuleWithPosition)currentMatcherWithPosition;
					longestStack.push(new RuleWithPosition(currentRuleWithPosition.getRule(), currentRuleWithPosition.getFromIndex(), currentRuleWithPosition.getToIndex()));
				}
			}
			
			this.longestParsingState = parsingState;
		}
		
		currentStack.pop();
	}
	
	public void printExtendedStackTrace(PrintStream stream) {
		stream.println("Source Snippet:");
		stream.println("---------------");
		// TODO: Integrate the error pointer directly into the source snippet
		displaySourceSnippet(stream);
		stream.println("---------------");
		
		stream.println();
		displayStackTrace(stream);

		stream.println();
		stream.println("Last successful tokens:");
		stream.println("-----------------------");
		displayLastSuccessfulTokens(stream);
	}
	
	private void displayLastSuccessfulTokens(PrintStream stream) {
		/* Display the LAST_SUCCESSFUL_TOKENS_WINDOW last successfully consumed tokens, and the name of the rule which consumed them */
		for (int i = longestIndex - 1; i >= longestIndex - LAST_SUCCESSFUL_TOKENS_WINDOW && i >= 0; i--) {
			Token token = longestParsingState.readToken(i);
			stream.println("  \"" + token.getValue().replace("\"", "\\\"") + "\" at " + token.getLine() + ":" + token.getColumn() + " consumed by " + getTokenConsumer(i).getRule().getName());
		}
	}
	
	private RuleWithPosition getTokenConsumer(int lexerIndex) {
		for (RuleWithPosition ruleWithPosition: longestStack) {
			if (ruleWithPosition.getFromIndex() <= lexerIndex && lexerIndex < ruleWithPosition.getToIndex()) {
				return ruleWithPosition;
			}
		}
		
		return null;
	}
	
	private void displayStackTrace(PrintStream stream) {
		if (longestStack.size() == 0) {
			stream.println("[ Not a single match ]");
		}
		else {
			stream.println("on matcher " + MatcherTreePrinter.print(longesOutertMatcherWithPosition.getMatcher()));
			stream.print(getPosition(longestParsingState.readToken(longestIndex).getLine(), longestParsingState.readToken(longestIndex).getColumn()));
			stream.print(MatcherTreePrinter.print(longestMatcherWithPosition.getMatcher()) + " expected but ");
			stream.println("\"" + longestParsingState.readToken(longestIndex).getValue().replace("\"", "\\\"") + "\"" + " [" + longestParsingState.readToken(longestIndex).getType() + "] found");
			
			for (int i = longestStack.size() - 1; i >= 0; i--) {
				stream.print("at ");
				displayStackTraceRuleWithPosition(stream, longestStack.get(i));
			}
		}
	}
	
	private void displayStackTraceRuleWithPosition(PrintStream stream, RuleWithPosition ruleWithPosition) {
		StringBuilder ruleBuilder = new StringBuilder();
		
		Token fromToken = longestParsingState.readToken(ruleWithPosition.getFromIndex());
		
		ruleBuilder.append(ruleWithPosition.getRule().getName() + System.getProperty("line.separator"));
		ruleBuilder.append(getPosition(fromToken.getLine(), fromToken.getColumn()));
		
		/* Display the next SOURCE_SNIPPET_NEXT_TOKENS tokens, until the toIndex */
		int i;
		for (i = ruleWithPosition.getFromIndex(); i < ruleWithPosition.getFromIndex() + STACK_TRACE_RULE_STARTING_WITH_TOKENS && i < ruleWithPosition.getToIndex() && i < longestParsingState.lexerSize; i++) {
			ruleBuilder.append(longestParsingState.readToken(i).getValue());
			ruleBuilder.append(" ");
		}

		/* Display "..." if there are still more tokens available after the last one displayed before the toIndex */
		if (i < ruleWithPosition.getToIndex() && i < longestParsingState.lexerSize) ruleBuilder.append("...");

		stream.println(ruleBuilder);
	}
	
	private String getPosition(int line, int column) {
		return "  " + String.format("%1$#" + LINE_AND_COLUMN_LEFT_PAD_LENGTH + "s", line) + " : " + String.format("%1$#" + LINE_AND_COLUMN_LEFT_PAD_LENGTH + "s", column) + "  : ";
	}
	
  private void displaySourceSnippet(PrintStream stream) {
  	Token failedToken = longestParsingState.readToken(longestIndex);
  	
    List<Token> tokens = getTokensToDisplayAroundOutpostMatcherToken();
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
    
    if (tokens.size() > 0) stream.println(lineBuilder.toString());
  }
  
  private void displayToken(StringBuilder lineBuilder, Token token) {
    while (lineBuilder.length() - LINE_AND_COLUMN_LEFT_PAD_LENGTH < token.getColumn()) {
      lineBuilder.append(" ");
    }
    lineBuilder.append(token.getValue());
  }
  
  private void displaySourceCodeLineHeader(StringBuilder lineBuilder, int currentLine, int parsingErrorLine) {
    String line = (parsingErrorLine == currentLine) ? "-->" : Integer.toString(currentLine);
    
    for (int i = 0; i < LINE_AND_COLUMN_LEFT_PAD_LENGTH - line.length() - 1; i++) {
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
