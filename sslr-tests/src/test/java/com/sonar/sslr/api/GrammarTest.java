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
package com.sonar.sslr.api;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.internal.matchers.GrammarException;

import java.lang.reflect.Field;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class GrammarTest {

  @org.junit.Rule
  public ExpectedException thrown = ExpectedException.none();

  private final MyGrammar grammar = new MyGrammar();

  @Test
  public void testGetRuleFields() {
    List<Field> ruleFields = Grammar.getRuleFields(MyGrammar.class);
    assertThat(ruleFields.size()).isEqualTo(1);
  }

  @Test
  public void testGetAllRuleFields() {
    List<Field> ruleFields = Grammar.getAllRuleFields(MyGrammar.class);
    assertThat(ruleFields.size()).isEqualTo(5);
  }

  @Test
  public void shouldAutomaticallyInstanciateDirectRules() {
    assertThat(grammar.rootRule).isNotNull();
  }

  @Test
  public void shouldAutomaticallyInstanciateInheritedRules() throws IllegalAccessException {
    List<Field> ruleFields = Grammar.getAllRuleFields(MyGrammar.class);
    for (Field ruleField : ruleFields) {
      ruleField.setAccessible(true);
      assertThat(ruleField.get(grammar)).as("Current rule name = " + ruleField.getName()).isNotNull();
    }
  }

  @Test
  public void should_throw_exception() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("Unable to instanciate the rule 'rootRule': Field is final");
    new IllegalGrammar();
  }

  public static abstract class MyBaseGrammar extends Grammar {
    Rule basePackageRule;
    public Rule basePublicRule;
    @SuppressWarnings("unused")
    private Rule basePrivateRule;
    protected Rule baseProtectedRule;
  }

  public static class MyGrammar extends MyBaseGrammar {
    @SuppressWarnings("unused")
    private int junkIntField;
    public Object junkObjectField;
    public Rule rootRule;

    @Override
    public Rule getRootRule() {
      return rootRule;
    }
  }

  private static class IllegalGrammar extends Grammar {
    private static final Rule rootRule = mock(Rule.class);

    @Override
    public Rule getRootRule() {
      return rootRule;
    }
  }

}
