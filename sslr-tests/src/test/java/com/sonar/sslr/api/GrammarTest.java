/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;

public class GrammarTest {

  private final MyGrammar grammar = new MyGrammar();

  @Test
  public void testGetRuleFields() {
    Field[] ruleFields = Grammar.getRuleFields(MyGrammar.class);
    assertThat(ruleFields.length, is(2));
  }

  @Test
  public void testGetAllRuleFields() {
    Field[] ruleFields = Grammar.getAllRuleFields(MyGrammar.class);
    assertThat(ruleFields.length, is(6));
  }

  @Test
  public void shouldAutomaticallyInstanciateDirectRules() {
    assertThat(grammar.leftRecursiveRule, is(notNullValue()));
    assertThat(grammar.rootRule, is(notNullValue()));
  }

  @Test
  public void shouldAutomaticallyInstanciateInheritedRules() throws IllegalAccessException {
    Field[] ruleFields = Grammar.getAllRuleFields(MyGrammar.class);

    for (Field ruleField : ruleFields) {
      ruleField.setAccessible(true);
      assertThat("Current rule name = " + ruleField.getName(), ruleField.get(grammar), is(notNullValue()));
    }
  }

  public abstract class MyBaseGrammar extends Grammar {

    Rule basePackageRule;
    public Rule basePublicRule;
    private Rule basePrivateRule;
    protected Rule baseProtectedRule;

  }

  public class MyGrammar extends MyBaseGrammar {

    private int junkIntField;
    public Object junkObjectField;
    public Rule rootRule;
    public LeftRecursiveRule leftRecursiveRule;

    @Override
    public Rule getRootRule() {
      return rootRule;
    }
  }

}
