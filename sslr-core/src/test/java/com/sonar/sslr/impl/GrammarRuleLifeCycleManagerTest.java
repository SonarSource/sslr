/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.LeftRecursiveRule;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.LeftRecursiveRuleMatcher;
import com.sonar.sslr.impl.matcher.RuleBuilder;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class GrammarRuleLifeCycleManagerTest {

  @Test
  public void testInitializeRuleFields() {
    SampleGrammar grammar = new SampleGrammar();

    assertThat(grammar.rule1, not(nullValue()));
    assertThat(grammar.rule1, is(RuleBuilder.class));
    assertThat(((RuleBuilder) grammar.rule1).getRule(), is(RuleMatcher.class));

    assertThat(grammar.rule2, not(nullValue()));
    assertThat(grammar.rule2, is(RuleBuilder.class));
    assertThat(((RuleBuilder) grammar.rule2).getRule(), is(LeftRecursiveRuleMatcher.class));
  }

  @Test
  public void reinitializeLeftRecursionRuleFields() {
    LeftRecursiveRuleMatcher mockRule = mock(LeftRecursiveRuleMatcher.class);
    SampleGrammar grammar = new SampleGrammar();
    grammar.rule1 = RuleBuilder.newRuleBuilder("rule1");
    grammar.rule2 = RuleBuilder.newRuleBuilder(mockRule);

    GrammarRuleLifeCycleManager.notifyEndParsing(grammar);

    verify(mockRule).endParsing();
  }

  public class SampleGrammar extends Grammar {

    public Rule rule1;
    @SuppressWarnings("unused")
    public LeftRecursiveRule rule2;

    public Rule getRootRule() {
      return rule1;
    }
  }

}
