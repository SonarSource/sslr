/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
