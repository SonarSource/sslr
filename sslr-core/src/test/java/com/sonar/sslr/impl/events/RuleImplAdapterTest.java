/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.events;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class RuleImplAdapterTest {

  private RuleMatcher rule;
  private String state = "";

  @Before
  public void init() {
    rule = RuleDefinition.newRuleBuilder("rule").is("bonjour").getRule();
  }

  @Test
  public void testGetDefinition() {
    RuleMatcherAdapter adapter = new RuleMatcherAdapter(null, rule);

    assertEquals(MatcherTreePrinter.printWithAdapters(adapter), "RuleImplAdapter(rule)");
    assertEquals(MatcherTreePrinter.print(adapter), "rule.is(\"bonjour\")");
  }

  @Test
  public void testMatch() {

    RuleMatcherAdapter adapter = new RuleMatcherAdapter(new ParsingEventListener() {

      public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState, RecognitionExceptionImpl re) {
        state += "exitWithoutMatchRule ";
      }

      public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, RecognitionExceptionImpl re) {
        state += "exitWithoutMatchMatcher ";
      }

      public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
        state += "exitWithMatchRule ";
      }

      public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
        state += "exitWithMatchMatcher ";
      }

      public void enterRule(RuleMatcher rule, ParsingState parsingState) {
        state += "enterRule ";
      }

      public void enterMatcher(Matcher matcher, ParsingState parsingState) {
        state += "enterMatcher ";
      }
    }, rule);

    state = "";
    assertThat(adapter, match("bonjour"));
    assertEquals(state, "enterRule exitWithMatchRule ");

    state = "";
    assertThat(adapter, not(match("test")));
    assertEquals(state, "enterRule exitWithoutMatchRule ");
  }

}
