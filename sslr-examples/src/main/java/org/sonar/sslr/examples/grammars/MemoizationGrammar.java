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
package org.sonar.sslr.examples.grammars;

import com.sonar.sslr.api.Grammar;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

public enum MemoizationGrammar implements GrammarRuleKey {

  A,
  B,
  C;

  public static Grammar requiresNegativeMemoization() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(A).is(
        'a',
        b.firstOf(
            b.sequence(A, 'b'),
            b.sequence(A, 'c')));

    return b.build();
  }

  public static Grammar requiresPositiveMemoization() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(A).is(
        b.firstOf(
            b.sequence(b.optional(B), 'a'),
            b.sequence(b.optional(B), 'b')));
    b.rule(B).is('(', A, ')');

    return b.build();
  }

  public static Grammar requiresPositiveMemoizationOnMoreThanJustLastRule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(A).is(
        b.firstOf(
            b.sequence(b.optional(B), 'a'),
            // rule 'C' will match and override the memoization result of 'B':
            b.sequence(C, '!'),
            b.sequence(b.optional(B), 'b')));
    b.rule(B).is('(', A, ')');
    // rule 'C' will override each following memoization result of 'A':
    b.rule(C).is('(', b.optional(C));

    return b.build();
  }

}
