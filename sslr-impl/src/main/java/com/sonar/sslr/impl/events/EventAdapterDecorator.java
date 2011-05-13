/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.util.HashSet;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class EventAdapterDecorator<GRAMMAR extends Grammar> implements GrammarDecorator<GRAMMAR> {
	private HashSet<Matcher> visited;
  private ParsingEventListener parsingEventListener;

  public EventAdapterDecorator(ParsingEventListener parsingEventListener) {
    this.parsingEventListener = parsingEventListener;
  }

  private void decorateMatcher(Matcher matcher) {
		/* Visitor logic */
		if (visited.contains(matcher)) return; /* This matcher was already visited */
		visited.add(matcher);
  	
    for (int i = 0; i < matcher.getChildren().length; i++) {
      decorateMatcher(matcher.getChildren()[i]); /* Recursive */

      if (matcher.getChildren()[i] instanceof RuleImpl) {
        matcher.getChildren()[i] = new RuleImplAdapter(parsingEventListener, (RuleImpl) matcher.getChildren()[i]);
      } else {
        matcher.getChildren()[i] = new MatcherAdapter(parsingEventListener, matcher.getChildren()[i]);
      }
    }
  }

  public void decorate(GRAMMAR grammar) {
    RuleImpl root = (RuleImpl) grammar.getRootRule();

    /* Change the root of the grammar, using reflection */
    try {
      grammar.getClass().getField(root.getName()).set(grammar, new RuleImplAdapter(parsingEventListener, root));
    } catch (Exception e) {
      throw new RuntimeException("Unable to decorate the root rule " + grammar.getClass().getName() + "." + root.getName() + " while adding the event support.");
    }

    visited = new HashSet<Matcher>();
    decorateMatcher(root); /* Change the whole tree, recursively! */
  }
  
  public RuleImplAdapter adaptRule(RuleImpl rule) {
  	return new RuleImplAdapter(parsingEventListener, rule);
  }
  
  public ParsingEventListener getParsingEventListener() {
  	return this.parsingEventListener;
  }

}
