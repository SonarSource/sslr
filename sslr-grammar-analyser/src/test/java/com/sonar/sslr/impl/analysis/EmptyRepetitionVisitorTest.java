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

import com.google.common.collect.Sets;
import com.sonar.sslr.impl.matcher.DelegatingMatcher;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Test;

import java.util.Set;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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

    assertThat(visitor.getEmptyRepetitions(), is((Set) Sets.newHashSet(DelegatingMatcher.unwrap(matcher))));
  }

  @Test
  public void emptyRepetitionInRuleTest() {
    EmptyRepetitionVisitor visitor = new EmptyRepetitionVisitor();

    RuleDefinition rule = RuleDefinition.newRuleBuilder("rule");
    Matcher matcher = one2n(opt("foo"));
    rule.is(matcher);

    visitor.visit(rule.getRule());

    assertThat(visitor.getEmptyRepetitions(), is((Set) Sets.newHashSet(DelegatingMatcher.unwrap(matcher))));
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
