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
 * JSON grammar. See <a href="http://json.org/">http://json.org/</a>.
 */
public enum JsonGrammar implements GrammarRuleKey {

  JSON,
  ARRAY,
  OBJECT,
  PAIR,
  VALUE,
  STRING,
  NUMBER,
  TRUE,
  FALSE,
  NULL,
  WHITESPACE;

  public static Grammar create() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(JSON).is(b.firstOf(ARRAY, OBJECT));
    b.rule(OBJECT).is("{", WHITESPACE, b.optional(PAIR, b.zeroOrMore(",", WHITESPACE, PAIR)), "}", WHITESPACE);
    b.rule(PAIR).is(STRING, ":", WHITESPACE, VALUE);
    b.rule(ARRAY).is("[", WHITESPACE, b.optional(VALUE, b.zeroOrMore(",", WHITESPACE, VALUE)), "]", WHITESPACE);
    b.rule(STRING).is('"', b.regexp("([^\"\\\\]|\\\\([\"\\\\/bfnrt]|u[0-9a-fA-F]{4}))*+"), '"', WHITESPACE);
    b.rule(VALUE).is(b.firstOf(STRING, NUMBER, OBJECT, ARRAY, TRUE, FALSE, NULL), WHITESPACE);
    b.rule(NUMBER).is(b.regexp("-?+(0|[1-9][0-9]*+)(\\.[0-9]++)?+([eE][+-]?+[0-9]++)?+"));
    b.rule(TRUE).is("true");
    b.rule(FALSE).is("false");
    b.rule(NULL).is("null");
    b.rule(WHITESPACE).is(b.regexp("[ \n\r\t\f]*+"));

    return b.build();
  }

}
