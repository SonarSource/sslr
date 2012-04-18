/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class GrammarTest {

  private final MyGrammar grammar = new MyGrammar();

  @Test
  public void testGetRuleFields() {
    List<Field> ruleFields = Grammar.getRuleFields(MyGrammar.class);
    assertThat(ruleFields.size(), is(1));
  }

  @Test
  public void testGetAllRuleFields() {
    List<Field> ruleFields = Grammar.getAllRuleFields(MyGrammar.class);
    assertThat(ruleFields.size(), is(5));
  }

  @Test
  public void shouldAutomaticallyInstanciateDirectRules() {
    assertThat(grammar.rootRule, is(notNullValue()));
  }

  @Test
  public void shouldAutomaticallyInstanciateInheritedRules() throws IllegalAccessException {
    List<Field> ruleFields = Grammar.getAllRuleFields(MyGrammar.class);

    for (Field ruleField : ruleFields) {
      ruleField.setAccessible(true);
      assertThat("Current rule name = " + ruleField.getName(), ruleField.get(grammar), is(notNullValue()));
    }
  }

  public abstract class MyBaseGrammar extends Grammar {

    Rule basePackageRule;
    public Rule basePublicRule;
    @SuppressWarnings("unused")
    private Rule basePrivateRule;
    protected Rule baseProtectedRule;

  }

  public class MyGrammar extends MyBaseGrammar {

    @SuppressWarnings("unused")
    private int junkIntField;
    public Object junkObjectField;
    public Rule rootRule;

    @Override
    public Rule getRootRule() {
      return rootRule;
    }
  }

}
