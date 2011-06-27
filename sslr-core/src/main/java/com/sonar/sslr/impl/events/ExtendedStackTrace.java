/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.util.Stack;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class ExtendedStackTrace extends ParsingEventListener {

  private Stack<MatcherWithPosition> currentStack;
  public Stack<RuleWithPosition> longestStack;
  public MatcherWithPosition longestOutertMatcherWithPosition;
  public MatcherWithPosition longestMatcherWithPosition;
  public int longestIndex;
  public ParsingState longestParsingState;

  public class MatcherWithPosition {

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

  public class RuleWithPosition extends MatcherWithPosition {

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
      return (RuleMatcher) getMatcher();
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
  	RuleWithPosition lastRuleWithPosition = getLastRuleWithPosition(currentStack);
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
  public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState) {
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
  public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState) {  	
    /* Handle the longest path */
    if (enforceLexerIndexUpperBound(parsingState.lexerIndex, parsingState.lexerSize) > longestIndex) {
      /* New longest path! */
    	longestIndex = enforceLexerIndexUpperBound(parsingState.lexerIndex, parsingState.lexerSize);
      longestMatcherWithPosition = currentStack.peek();

      /* Set the longest matcher to the outer most one starting at the same index as the current matcher */
      longestOutertMatcherWithPosition = getOuterMatcherWithPosition(currentStack, currentStack.peek().getFromIndex());
      
      /* Set the current's rule toIndex */
      RuleWithPosition lastRuleWithPosition = getLastRuleWithPosition(currentStack);
      if (lastRuleWithPosition != null) {
        lastRuleWithPosition.setToIndex(parsingState.lexerIndex);
      }

      /* Copy the current stack to the longest stack (deep-copy / clone) */
      longestStack = new Stack<RuleWithPosition>();
      for (MatcherWithPosition currentMatcherWithPosition : currentStack) {
        if (currentMatcherWithPosition instanceof RuleWithPosition) {
          RuleWithPosition currentRuleWithPosition = (RuleWithPosition) currentMatcherWithPosition;
          longestStack.push(new RuleWithPosition(currentRuleWithPosition.getRule(), currentRuleWithPosition.getFromIndex(), currentRuleWithPosition.getToIndex()));
        }
      }

      this.longestParsingState = parsingState;
    }

    currentStack.pop();
  }
  
  private static int enforceLexerIndexUpperBound(int index, int lexerSize) {
  	return (index < lexerSize) ? index : lexerSize - 1;
  }
  
  private static RuleWithPosition getLastRuleWithPosition(Stack<MatcherWithPosition> stack) {
  	for (int i = stack.size() - 1; i >= 0; i--) {
  		if (stack.elementAt(i) instanceof RuleWithPosition) {
  			return (RuleWithPosition)stack.elementAt(i);
  		}
  	}
  	
  	return null;
  }
  
  private static MatcherWithPosition getOuterMatcherWithPosition(Stack<MatcherWithPosition> stack, int lexerIndex) {
  	for (MatcherWithPosition matcherWithPosition: stack) {
  		if (matcherWithPosition.getFromIndex() == lexerIndex) {
  			return matcherWithPosition;
  		}
  	}
  	
  	return null;
  }

}
