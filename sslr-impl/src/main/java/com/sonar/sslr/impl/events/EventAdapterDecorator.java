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
import com.sonar.sslr.impl.matcher.RuleBuilder;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class EventAdapterDecorator<GRAMMAR extends Grammar> implements GrammarDecorator<GRAMMAR> {

  private HashSet<Matcher> visited;
  private ParsingEventListener parsingEventListener;

  public EventAdapterDecorator(ParsingEventListener parsingEventListener) {
    this.parsingEventListener = parsingEventListener;
  }

  private void decorateMatcher(Matcher matcher) {
    /* Visitor logic */
    if (visited.contains(matcher))
      return; /* This matcher was already visited */
    visited.add(matcher);

    for (int i = 0; i < matcher.getChildren().length; i++) {
      decorateMatcher(matcher.getChildren()[i]); /* Recursive */

      if (matcher.getChildren()[i] instanceof RuleMatcher) {
        matcher.getChildren()[i] = new RuleMatcherAdapter(parsingEventListener, (RuleMatcher) matcher.getChildren()[i]);
      } else {
        matcher.getChildren()[i] = new MatcherAdapter(parsingEventListener, matcher.getChildren()[i]);
      }
    }
  }

  public void decorate(GRAMMAR grammar) {
    RuleBuilder root = (RuleBuilder) grammar.getRootRule();

    RuleMatcher rule = root.getRule();

    root.replaceRuleMatcher(new RuleMatcherAdapter(parsingEventListener, rule));

    visited = new HashSet<Matcher>();
    decorateMatcher(rule); /* Change the whole tree, recursively! */
  }

  public ParsingEventListener getParsingEventListener() {
    return this.parsingEventListener;
  }

}
