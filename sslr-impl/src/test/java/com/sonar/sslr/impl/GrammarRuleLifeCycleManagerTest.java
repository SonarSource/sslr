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
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.LeftRecursiveRuleImpl;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class GrammarRuleLifeCycleManagerTest {

  @Test
  public void testInitializeRuleFields() {
    SampleGrammar grammar = new SampleGrammar();
    assertThat(grammar.rule1, nullValue());

    GrammarRuleLifeCycleManager.initializeRuleFields(grammar, SampleGrammar.class);

    assertThat(grammar.rule1, not(nullValue()));
    assertThat(grammar.rule1, is(RuleImpl.class));
  }

  @Test
  public void initializeLeftRecursionRuleFields() {
    SampleGrammar grammar = new SampleGrammar();
    assertThat(grammar.rule1, nullValue());

    GrammarRuleLifeCycleManager.initializeLeftRecursionRuleFields(grammar, SampleGrammar.class);

    assertThat(grammar.rule1, not(nullValue()));
    assertThat(grammar.rule1, is(LeftRecursiveRuleImpl.class));
  }

  @Test
  public void reinitializeLeftRecursionRuleFields() {
    LeftRecursiveRuleImpl mockRule = mock(LeftRecursiveRuleImpl.class);
    SampleGrammar grammar = new SampleGrammar();
    grammar.rule1 = new RuleImpl("rule1");
    grammar.rule2 = mockRule;

    GrammarRuleLifeCycleManager.reinitializeLeftRecursionRuleFields(grammar);

    verify(mockRule).reInitState();
  }

  private class SampleGrammar implements Grammar {

    public Rule rule1;
    @SuppressWarnings("unused")
    public Rule rule2;

    public Rule getRootRule() {
      return rule1;
    }
  }

}
