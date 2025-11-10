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
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

/**
 * Regular Expression Grammar.
 */
public enum RegularExpressionGrammar implements GrammarRuleKey {

  EXPRESSION,

  ALTERNATION,
  ALTERNATIVE,

  TERM,
  CAPTURING_GROUP,
  NON_CAPTURING_GROUP,
  POSITIVE_LOOKAHEAD,
  NEGATIVE_LOOKAHEAD,
  QUANTIFIER,
  NUMBER,

  ATOM,
  BACK_REFERENCE,

  CHARACTER_CLASS,
  CLASS_ATOM;

  public static Grammar create() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(EXPRESSION).is(ALTERNATION);

    b.rule(ALTERNATION).is(ALTERNATIVE, b.zeroOrMore("|", ALTERNATIVE));
    b.rule(ALTERNATIVE).is(b.zeroOrMore(TERM));

    b.rule(TERM).is(
      b.firstOf(
        ATOM,
        POSITIVE_LOOKAHEAD,
        NEGATIVE_LOOKAHEAD,
        NON_CAPTURING_GROUP,
        CAPTURING_GROUP
      ),
      b.optional(QUANTIFIER)
    );
    b.rule(POSITIVE_LOOKAHEAD).is("(?=", ALTERNATION, ")");
    b.rule(NEGATIVE_LOOKAHEAD).is("(?!", ALTERNATION, ")");
    b.rule(CAPTURING_GROUP).is("(", ALTERNATION, ")");
    b.rule(NON_CAPTURING_GROUP).is("(?:", ALTERNATION, ")");
    b.rule(QUANTIFIER).is(
      b.firstOf(
        "?",
        "*",
        "+",
        b.sequence("{", NUMBER, "}"),
        b.sequence("{", NUMBER, ",", "}"),
        b.sequence("{", NUMBER, ",", NUMBER, "}")
      ),
      b.optional(b.firstOf("+", "?"))
    );
    b.rule(NUMBER).is(b.regexp("[0-9]++"));

    b.rule(ATOM).is(b.firstOf(
      ".",
      BACK_REFERENCE,
      b.sequence(b.nextNot(b.firstOf('[', '*', '+', '?', '(', ')', '|')), b.regexp(".")),
      CHARACTER_CLASS
    ));
    b.rule(BACK_REFERENCE).is("\\", b.regexp("[1-9]"));

    b.rule(CHARACTER_CLASS).is('[', b.optional("^"), b.oneOrMore(CLASS_ATOM), ']');
    b.rule(CLASS_ATOM).is(b.firstOf(
      b.sequence('\\', b.regexp(".")),
      b.sequence(b.optional("&&"), CHARACTER_CLASS),
      b.sequence(b.nextNot(b.firstOf('[', ']', "&&")), b.regexp("."))
    ));

    return b.build();
  }

}
