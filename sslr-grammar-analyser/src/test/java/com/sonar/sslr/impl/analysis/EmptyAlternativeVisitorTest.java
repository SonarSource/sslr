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
