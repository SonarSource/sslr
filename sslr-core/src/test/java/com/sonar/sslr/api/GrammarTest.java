/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.api;

import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Test;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.lang.reflect.Field;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class GrammarTest {

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
  public void method_rule_should_throw_exception_by_default() {
    assertThrows(UnsupportedOperationException.class,
      () -> new MyGrammar().rule(mock(GrammarRuleKey.class)));
  }

  @Test
  public void should_automatically_instanciate_lexerful_rules() throws IllegalAccessException {
    List<Field> ruleFields = Grammar.getAllRuleFields(MyGrammar.class);
    Grammar grammar = new MyGrammar();
    for (Field ruleField : ruleFields) {
      ruleField.setAccessible(true);
      assertThat(ruleField.get(grammar)).as("Current rule name = " + ruleField.getName()).isNotNull().isInstanceOf(RuleDefinition.class);
    }
  }

  @Test
  public void should_automatically_instanciate_lexerless_rules() throws IllegalAccessException {
    List<Field> ruleFields = Grammar.getAllRuleFields(MyLexerlessGrammar.class);
    LexerlessGrammar grammar = new MyLexerlessGrammar();
    for (Field ruleField : ruleFields) {
      ruleField.setAccessible(true);
      assertThat(ruleField.get(grammar)).as("Current rule name = " + ruleField.getName()).isNotNull().isInstanceOf(MutableParsingRule.class);
    }
  }

  @Test
  public void should_throw_exception() {
    GrammarException thrown = assertThrows(GrammarException.class,
      IllegalGrammar::new);
    assertThat(thrown.getMessage()).startsWith("Unable to instanciate the rule 'rootRule': ");
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

  private static class MyLexerlessGrammar extends LexerlessGrammar {
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
