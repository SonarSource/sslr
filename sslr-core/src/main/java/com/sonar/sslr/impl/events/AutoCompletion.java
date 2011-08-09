/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.*;

import java.util.*;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.TokenMatcher;
import com.sonar.sslr.impl.matcher.TokenValueMatcher;

public final class AutoCompletion extends ParsingEventListener {

  private static final int maxTokens = 5;
  
  private final int maxLength;
  private final Matcher matcher;
  private final List<List<Token>> partialMatches = new LinkedList<List<Token>>();
  private final List<List<Token>> fullMatches = new LinkedList<List<Token>>();
  private List<List<Token>> prefixes = new LinkedList<List<Token>>();
  private final Set<TokenMatcher> followingTokenMatchers = new HashSet<TokenMatcher>();
  
  @Override
  public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState) {
    if (parsingState.lexerIndex == parsingState.lexerSize && matcher instanceof TokenMatcher) {
      followingTokenMatchers.add((TokenMatcher)matcher);
    }
  }
  
  public AutoCompletion(Matcher matcher) {
    this(matcher, new ArrayList<Token>());
  }
  
  public AutoCompletion(Matcher matcher, List<Token> tokens) {
    this(matcher, tokens, maxTokens);
  }
  
  public AutoCompletion(Matcher matcher, List<Token> tokens, int maxTokens) {
    this.matcher = matcher;
    this.maxLength = tokens.size() + maxTokens;
    this.prefixes.add(tokens);
  }
  
  private Token tokenMatcherToToken(TokenMatcher tokenMatcher) {
    if (tokenMatcher instanceof TokenValueMatcher) {
      String value = tokenMatcher.toString();
      value = value.substring(1);
      value = value.substring(0, value.length() - 1);
      return new Token(GenericTokenType.LITERAL, value);
    } else {
      throw new UnsupportedOperationException("tokenMatcherToToken() does not handle class " + tokenMatcher.getClass());
    }
  }

  public void autoComplete() {
    while (!prefixes.isEmpty()) {
      List<List<Token>> newPrefixes = new LinkedList<List<Token>>();
      for (List<Token> prefix: prefixes) {
        
    	  ParsingState parsingState = new ParsingState(prefix);
    	  parsingState.parsingEventListeners = new ParsingEventListener[] { this };
    	  
    	  try {
    	    followingTokenMatchers.clear();
    	    matcher.reinitializeMatcherTree();
    	    matcher.match(parsingState);
    	    
    	    /* The parse was actually successful, there is nothing to complete! */
    	    fullMatches.add(prefix);
    	  } catch (BacktrackingEvent re) {
    	    if (parsingState.lexerIndex < parsingState.lexerSize) {
    	      /* The given tokens are not a valid prefix for this matcher, failed to auto complete */
    	      throw new IllegalStateException("parsingState.lexerIndex should never be smaller than parsingState.lexerSize!");
    	    }
    	    
    	    if (followingTokenMatchers.isEmpty()) {
    	      /* The matcher did not match, but did also not consume any token matcher */
    	      throw new IllegalStateException("Matcher did not match but did not consume any TokenMatcher!");
    	    }
    	    
    	    /* completeCandidates contains the candidates for auto completion! */
    	    for (TokenMatcher followingTokenMatcher: followingTokenMatchers) {
    	      LinkedList<Token> newPrefix = new LinkedList<Token>();
    	      newPrefix.addAll(prefix);
    	      newPrefix.add(tokenMatcherToToken(followingTokenMatcher));
    	      
    	      if (newPrefix.size() >= maxLength) {
    	        partialMatches.add(newPrefix);
    	      } else {
    	        newPrefixes.add(newPrefix);
    	      }
    	    }
    	  }
      }
      prefixes = newPrefixes;
    }
	}
	
	public static void main(String[] args) {
	  
	  AutoCompletion auto = new AutoCompletion(
	      and("hello", or("Foo", "Bar"))
	  );
	  
	  auto.autoComplete();

    System.out.println("Full matches:");
    for (List<Token> tokens: auto.fullMatches) {
      System.out.print(" - ");
      for (Token token: tokens) {
        System.out.print(token.getValue() + " ");
      }
      System.out.println();
    }
    System.out.println();
    
    System.out.println("Partial matches:");
    for (List<Token> tokens: auto.partialMatches) {
      System.out.print(" - ");
      for (Token token: tokens) {
        System.out.print(token.getValue() + " ");
      }
      System.out.println();
    }
    
	}
	
}
