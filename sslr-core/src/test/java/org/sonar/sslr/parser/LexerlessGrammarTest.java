/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
package org.sonar.sslr.parser;

import com.sonar.sslr.api.Rule;
import org.junit.Test;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.internal.grammar.MutableParsingRule;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class LexerlessGrammarTest {

  @Test
  public void should_instanciate_rule_fields() {
    TestGrammar grammar = new TestGrammar();
    assertThat(grammar.getRootRule()).isInstanceOf(MutableParsingRule.class);
    assertThat(((MutableParsingRule) grammar.getRootRule()).getName()).isEqualTo("rootRule");
  }

  @Test
  public void should_throw_exception() {
    GrammarException thrown = assertThrows(GrammarException.class,
      IllegalGrammar::new);
    assertThat(thrown.getMessage()).startsWith("Unable to instanciate the rule 'rootRule': ");
  }

  private static class TestGrammar extends LexerlessGrammar {
    private Rule rootRule;

    @Override
    public Rule getRootRule() {
      return rootRule;
    }
  }

  private static class IllegalGrammar extends LexerlessGrammar {
    private static final Rule rootRule = mock(Rule.class);

    @Override
    public Rule getRootRule() {
      return rootRule;
    }
  }

}
