/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.examples.grammars.typed;

import com.sonar.sslr.api.GenericTokenType;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

public enum JsonLexer implements GrammarRuleKey {

  JSON,
  OBJECT,
  ARRAY,
  VALUE,
  TRUE,
  FALSE,
  NULL,
  STRING,
  NUMBER,

  LCURLYBRACE,
  RCURLYBRACE,
  LBRACKET,
  RBRACKET,
  COMMA,
  COLON,

  EOF,
  WHITESPACE,

  ;

  public static LexerlessGrammarBuilder createGrammarBuilder() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    punctuator(b, LCURLYBRACE, "{");
    punctuator(b, RCURLYBRACE, "}");
    punctuator(b, LBRACKET, "[");
    punctuator(b, RBRACKET, "]");
    punctuator(b, COMMA, ",");
    punctuator(b, COLON, ":");

    b.rule(TRUE).is("true", WHITESPACE);
    b.rule(FALSE).is("false", WHITESPACE);
    b.rule(NULL).is("null", WHITESPACE);

    b.rule(WHITESPACE).is(b.regexp("[ \n\r\t\f]*+"));
    b.rule(NUMBER).is(b.regexp("-?+(0|[1-9][0-9]*+)(\\.[0-9]++)?+([eE][+-]?+[0-9]++)?+"), WHITESPACE);
    b.rule(STRING).is(b.regexp("\"([^\"\\\\]|\\\\([\"\\\\/bfnrt]|u[0-9a-fA-F]{4}))*+\""), WHITESPACE);

    b.rule(EOF).is(b.token(GenericTokenType.EOF, b.endOfInput()));

    b.setRootRule(JSON);
    return b;
  }

  private static void punctuator(LexerlessGrammarBuilder b, GrammarRuleKey ruleKey, String value) {
    b.rule(ruleKey).is(value, WHITESPACE);
  }
}
