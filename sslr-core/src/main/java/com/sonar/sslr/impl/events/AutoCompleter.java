/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.util.*;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.*;

public final class AutoCompleter extends ParsingEventListener {

  private static final int maxTokens = 5;
  
  private final List<List<Token>> partialMatches = new LinkedList<List<Token>>();
  private final List<List<Token>> fullMatches = new LinkedList<List<Token>>();
  private final Set<TokenMatcher> followingTokenMatchers = new HashSet<TokenMatcher>();
  
  private int maxLength;
  private List<List<Token>> prefixes = new LinkedList<List<Token>>();
  private int predicateLevel;
  
  private boolean isPredicateMatcher(Matcher matcher) {
    return (matcher instanceof NotMatcher || matcher instanceof NextMatcher);
  }
  
  private Token tokenMatcherToToken(TokenMatcher tokenMatcher) {
    if (tokenMatcher instanceof TokenValueMatcher) {
      return new Token(GenericTokenType.LITERAL, ((TokenValueMatcher)tokenMatcher).getTokenValue());
    } else if (tokenMatcher instanceof TokenTypeMatcher) {
      return new Token(((TokenTypeMatcher)tokenMatcher).getType(), ((TokenTypeMatcher)tokenMatcher).getType().getValue());
    } else {
      throw new UnsupportedOperationException("tokenMatcherToToken() does not handle class " + tokenMatcher.getClass());
    }
  }
  
  @Override
  public void enterMatcher(Matcher matcher, ParsingState parsingState) {
    if (isPredicateMatcher(matcher)) predicateLevel++;
  }
  
  @Override
  public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    if (isPredicateMatcher(matcher)) predicateLevel--;
  }

  @Override
  public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState) {
    if (predicateLevel == 0 && parsingState.lexerIndex == parsingState.lexerSize && matcher instanceof TokenMatcher) {
      followingTokenMatchers.add((TokenMatcher)matcher);
    }
    
    if (isPredicateMatcher(matcher)) predicateLevel--;
  }
  
  public void autoComplete(Matcher matcher) {
    autoComplete(matcher, new ArrayList<Token>());
  }
  
  public void autoComplete(Matcher matcher, List<Token> tokens) {
    autoComplete(matcher, tokens, maxTokens);
  }

  public void autoComplete(Matcher matcher, List<Token> tokens, int maxTokens) {
    this.maxLength = tokens.size() + maxTokens;
    this.prefixes.clear();
    this.partialMatches.clear();
    this.fullMatches.clear();
    this.prefixes.add(tokens);
    predicateLevel = 0;
    
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
    	    if (followingTokenMatchers.isEmpty()) {
    	      /* Overall the matcher did not match, but no progress was made */
    	      throw new IllegalStateException("Matcher did not match and did not consume any additional TokenMatcher (this should never happen when using only Standard matchers).");
    	    }
    	    
    	    if (prefix.size() < maxLength) {
      	    /* completeCandidates contains the candidates for auto completion! */
      	    for (TokenMatcher followingTokenMatcher: followingTokenMatchers) {
      	      LinkedList<Token> newPrefix = new LinkedList<Token>();
      	      newPrefix.addAll(prefix);
      	      newPrefix.add(tokenMatcherToToken(followingTokenMatcher));
  
      	      newPrefixes.add(newPrefix);
      	    }
    	    } else {
    	      partialMatches.add(prefix);
    	    }
    	  }
      }
      prefixes = newPrefixes;
    }
	}
  
  public List<List<Token>> getFullMatches() {
    return fullMatches;
  }
  
  public List<List<Token>> getPartialMatches() {
    return partialMatches;
  }
	
}
