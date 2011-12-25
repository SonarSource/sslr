/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class GrammarTest {

  private final MyGrammar grammar = new MyGrammar();

  @Test
  public void shouldIndexRules() {
    assertThat(grammar.findRuleByName("rootRule"), is(grammar.getRootRule()));
    assertThat(grammar.findRuleByName("leftRecursiveRule"), is(notNullValue()));
    assertThat(grammar.findRuleByName("unknownRule"), is(nullValue()));
  }

  @Test
  public void shouldAutomaticallyInstanciateRules() {
    assertThat(grammar.leftRecursiveRule, is(notNullValue()));
    assertThat(grammar.rootRule, is(notNullValue()));
  }

  public class MyGrammar extends Grammar {

    public Rule rootRule;
    public LeftRecursiveRule leftRecursiveRule;

    @Override
    public Rule getRootRule() {
      return rootRule;
    }
  }
}
