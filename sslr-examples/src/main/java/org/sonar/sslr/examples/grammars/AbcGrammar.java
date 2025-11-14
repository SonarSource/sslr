/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

/**
 * Grammar for the classic non-context-free language { a^n b^n c^n : n >= 1 }.
 */
public enum AbcGrammar implements GrammarRuleKey {

  S, A, B;

  public static Grammar createGrammar() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(S).is(b.next(A, "c"), b.oneOrMore("a"), B, b.nextNot("a", "b", "c"));
    b.rule(A).is("a", b.optional(A), "b");
    b.rule(B).is("b", b.optional(B), "c");

    return b.build();
  }

}
