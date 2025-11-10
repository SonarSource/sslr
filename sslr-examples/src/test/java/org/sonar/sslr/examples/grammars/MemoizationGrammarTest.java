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
package org.sonar.sslr.examples.grammars;

import com.sonar.sslr.api.Grammar;
import org.junit.Test;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class MemoizationGrammarTest {

  @Test
  public void should_be_slow_to_fail_to_parse_gramar_requiring_negative_memoization() {
    Grammar grammar = MemoizationGrammar.requiresNegativeMemoization();
    assertThat(grammar.rule(MemoizationGrammar.A))
        .notMatches("aaaaaaaaaaaaaaa") // Requires time T
        .notMatches("aaaaaaaaaaaaaaaa") // Requires time 2*T
        .notMatches("aaaaaaaaaaaaaaaaa"); // Requires time 4*T, etc.
  }

  @Test
  public void should_be_fast_on_grammar_requiring_positive_memoization() {
    Grammar grammar = MemoizationGrammar.requiresPositiveMemoization();
    assertThat(grammar.rule(MemoizationGrammar.A))
        .matches("((((((((((((((((((((((((b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b") // Requires time T
        .matches("((((((((((((((((((((((((((b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b") // Requires time ~T
        .matches("((((((((((((((((((((((((((((b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b"); // Requires time ~T, etc.
  }

  @Test
  public void should_be_slow_on_grammar_requiring_positive_memoization_on_more_than_just_the_last_rule() {
    Grammar grammar = MemoizationGrammar.requiresPositiveMemoizationOnMoreThanJustLastRule();
    assertThat(grammar.rule(MemoizationGrammar.A))
        .matches("(((((((((((((((b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b") // Requires time T
        .matches("(((((((((((((((((b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b") // Requires time 4*T
        .matches("(((((((((((((((((((b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b)b"); // Requires time 16*T, etc.
  }
}
