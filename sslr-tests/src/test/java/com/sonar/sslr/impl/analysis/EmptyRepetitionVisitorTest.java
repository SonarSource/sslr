/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Sets;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Test;

import java.util.Set;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class EmptyRepetitionVisitorTest {

  @Test
  public void noRepetitionTest() {
    EmptyRepetitionVisitor visitor = new EmptyRepetitionVisitor();

    visitor.visit(and("foo"));
    assertThat(visitor.getEmptyRepetitions().size(), is(0));
  }

  @Test
  public void noEmptyRepetitionTest() {
    EmptyRepetitionVisitor visitor = new EmptyRepetitionVisitor();

    visitor.visit(one2n("foo"));
    assertThat(visitor.getEmptyRepetitions().size(), is(0));
  }

  @Test
  public void emptyRepetitionTest() {
    EmptyRepetitionVisitor visitor = new EmptyRepetitionVisitor();

    Matcher matcher = one2n(opt("foo"));
    visitor.visit(matcher);

    assertThat(visitor.getEmptyRepetitions(), is((Set) Sets.newHashSet(matcher)));
  }

  @Test
  public void emptyRepetitionInRuleTest() {
    EmptyRepetitionVisitor visitor = new EmptyRepetitionVisitor();

    RuleDefinition rule = RuleDefinition.newRuleBuilder("rule");
    Matcher matcher = one2n(opt("foo"));
    rule.is(matcher);

    visitor.visit(rule.getRule());

    assertThat(visitor.getEmptyRepetitions(), is((Set) Sets.newHashSet(matcher)));
  }

  @Test
  public void emptyRepetitionInDifferentRuleTest() {
    EmptyRepetitionVisitor visitor = new EmptyRepetitionVisitor();

    RuleDefinition ruleA = RuleDefinition.newRuleBuilder("ruleA");
    RuleDefinition ruleB = RuleDefinition.newRuleBuilder("ruleB");

    ruleA.is(ruleB);
    ruleB.is(one2n(opt("foo")));

    visitor.visit(ruleA.getRule());

    assertThat(visitor.getEmptyRepetitions().size(), is(0));
  }

}
