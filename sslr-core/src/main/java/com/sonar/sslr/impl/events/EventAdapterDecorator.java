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
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class EventAdapterDecorator<GRAMMAR extends Grammar> implements GrammarDecorator<GRAMMAR> {

  private HashSet<Matcher> visited;
  private ParsingEventListener[] parsingEventListeners;

  public EventAdapterDecorator(ParsingEventListener... parsingEventListeners) {
    this.parsingEventListeners = parsingEventListeners;
  }

  private void decorateMatcher(Matcher matcher) {
    /* Visitor logic */
    if (visited.contains(matcher))
      return; /* This matcher was already visited */
    visited.add(matcher);

    for (int i = 0; i < matcher.getChildren().length; i++) {
      decorateMatcher(matcher.getChildren()[i]); /* Recursive */

      if (matcher.getChildren()[i] instanceof RuleMatcher) {
        matcher.getChildren()[i] = new RuleMatcherAdapter((RuleMatcher) matcher.getChildren()[i], parsingEventListeners);
      } else {
        matcher.getChildren()[i] = new MatcherAdapter(matcher.getChildren()[i], parsingEventListeners);
      }
    }
  }

  public void decorate(GRAMMAR grammar) {
    RuleDefinition root = (RuleDefinition) grammar.getRootRule();

    RuleMatcher rule = root.getRule();

    root.setRuleMatcher(new RuleMatcherAdapter(rule, parsingEventListeners));

    visited = new HashSet<Matcher>();
    decorateMatcher(rule); /* Change the whole tree, recursively! */
  }

  public ParsingEventListener[] getParsingEventListeners() {
    return this.parsingEventListeners;
  }

}
