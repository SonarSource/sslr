/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;
import org.junit.Test;

import java.util.Stack;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class LeftRecursiveExceptionTest {

  @Test(expected = NullPointerException.class)
  public void illegalNullStackTest() {
    new LeftRecursionException(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalEmptyStack() {
    new LeftRecursionException(new Stack<RuleMatcher>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalSingleElementStackTest() {
    RuleMatcher rule = RuleDefinition.newRuleBuilder("rule").is("foo").getRule();

    Stack<RuleMatcher> stack = new Stack<RuleMatcher>();
    stack.push(rule);

    new LeftRecursionException(stack);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalNoLeftRecursionTest() {
    RuleMatcher rule1 = RuleDefinition.newRuleBuilder("rule1").is("foo").getRule();
    RuleMatcher rule2 = RuleDefinition.newRuleBuilder("rule2").is("foo").getRule();

    Stack<RuleMatcher> stack = new Stack<RuleMatcher>();
    stack.push(rule1);
    stack.push(rule2);

    new LeftRecursionException(stack);
  }

  @Test
  public void directLeftRecursionTest() {
    RuleMatcher rule = RuleDefinition.newRuleBuilder("rule").is("foo").getRule();

    Stack<RuleMatcher> stack = new Stack<RuleMatcher>();
    stack.push(rule);
    stack.push(rule);

    LeftRecursionException e = new LeftRecursionException(stack);
    assertThat(e.getLeftRecursiveRule(), is(rule));
  }

  @Test
  public void leftRecursionInDifferentRule() {
    RuleMatcher rule = RuleDefinition.newRuleBuilder("rule").is("foo").getRule();
    RuleMatcher leftRecursiveRule = RuleDefinition.newRuleBuilder("leftRecursiveRule").is("foo").getRule();

    Stack<RuleMatcher> stack = new Stack<RuleMatcher>();
    stack.push(rule);
    stack.push(leftRecursiveRule);
    stack.push(leftRecursiveRule);

    LeftRecursionException e = new LeftRecursionException(stack);
    assertThat(e.getLeftRecursiveRule(), is(leftRecursiveRule));
  }

  @Test
  public void indirectLeftRecursion() {
    RuleMatcher leftRecursiveRule = RuleDefinition.newRuleBuilder("leftRecursiveRule").is("foo").getRule();
    RuleMatcher rule = RuleDefinition.newRuleBuilder("rule").is("foo").getRule();

    Stack<RuleMatcher> stack = new Stack<RuleMatcher>();
    stack.push(leftRecursiveRule);
    stack.push(rule);
    stack.push(leftRecursiveRule);

    LeftRecursionException e = new LeftRecursionException(stack);
    assertThat(e.getLeftRecursiveRule(), is(leftRecursiveRule));
  }

}
