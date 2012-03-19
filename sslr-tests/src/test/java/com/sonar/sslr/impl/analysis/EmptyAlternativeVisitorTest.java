/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Sets;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.OrMatcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Test;

import java.util.Set;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class EmptyAlternativeVisitorTest {

  @Test
  public void noAlternativeTest() {
    EmptyAlternativeVisitor visitor = new EmptyAlternativeVisitor();

    visitor.visit(and("foo"));
    assertThat(visitor.getEmptyAlternatives().size(), is(0));
  }

  @Test
  public void noEmptyAlternativeTest() {
    EmptyAlternativeVisitor visitor = new EmptyAlternativeVisitor();

    visitor.visit(or("foo", "bar"));
    assertThat(visitor.getEmptyAlternatives().size(), is(0));
  }

  @Test
  public void emptyAlternativeTest() {
    EmptyAlternativeVisitor visitor = new EmptyAlternativeVisitor();

    Matcher alternative = opt("foo");
    OrMatcher orMatcher = (OrMatcher) or(alternative, "foo", "bar");
    visitor.visit(orMatcher);

    assertThat(visitor.getEmptyAlternatives(), is((Set) Sets.newHashSet(new EmptyAlternative(orMatcher, alternative))));
  }

  @Test
  public void emptyAlternativeInRuleTest() {
    EmptyAlternativeVisitor visitor = new EmptyAlternativeVisitor();

    RuleDefinition rule = RuleDefinition.newRuleBuilder("rule");
    Matcher alternative = opt("foo");
    OrMatcher orMatcher = (OrMatcher) or(alternative, "foo", "bar");
    rule.is(orMatcher);

    visitor.visit(rule.getRule());

    assertThat(visitor.getEmptyAlternatives(), is((Set) Sets.newHashSet(new EmptyAlternative(orMatcher, alternative))));
  }

  @Test
  public void emptyAlternativeInDifferentRuleTest() {
    EmptyAlternativeVisitor visitor = new EmptyAlternativeVisitor();

    RuleDefinition ruleA = RuleDefinition.newRuleBuilder("ruleA");
    RuleDefinition ruleB = RuleDefinition.newRuleBuilder("ruleB");

    ruleA.is(ruleB);
    ruleB.is(or("foo", opt("bar")));

    visitor.visit(ruleA.getRule());

    assertThat(visitor.getEmptyAlternatives().size(), is(0));
  }

}
