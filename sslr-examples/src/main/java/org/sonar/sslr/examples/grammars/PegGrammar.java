/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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
package org.sonar.sslr.examples.grammars;

import com.sonar.sslr.api.Grammar;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

/**
 * PEG grammar.
 */
public enum PegGrammar implements GrammarRuleKey {

  GRAMMAR,
  RULE,
  RULE_KEY,
  STRING,
  ATOM,

  FIRST_OF_EXPRESSION,
  SEQUENCE_EXPRESSION,
  ZERO_OR_MORE_EXPRESSION,
  ONE_OR_MORE_EXPRESSION,
  OPTIONAL_EXPRESSION,
  NEXT_NOT_EXPRESSION,
  NEXT_EXPRESSION,

  ASSIGN,
  SEMICOLON,
  OR,
  NOT,
  AND,
  QUESTION,
  PLUS,
  STAR,
  OPEN,
  CLOSE,
  WHITESPACE;

  public static Grammar create() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(GRAMMAR).is(WHITESPACE, b.zeroOrMore(RULE), b.endOfInput());
    b.rule(RULE).is(RULE_KEY, ASSIGN, FIRST_OF_EXPRESSION, SEMICOLON);
    b.rule(FIRST_OF_EXPRESSION).is(SEQUENCE_EXPRESSION, b.zeroOrMore(OR, SEQUENCE_EXPRESSION));
    b.rule(SEQUENCE_EXPRESSION).is(b.oneOrMore(b.firstOf(
      ZERO_OR_MORE_EXPRESSION,
      ONE_OR_MORE_EXPRESSION,
      OPTIONAL_EXPRESSION,
      NEXT_NOT_EXPRESSION,
      NEXT_EXPRESSION,
      ATOM)));
    b.rule(ZERO_OR_MORE_EXPRESSION).is(ATOM, STAR);
    b.rule(ONE_OR_MORE_EXPRESSION).is(ATOM, PLUS);
    b.rule(OPTIONAL_EXPRESSION).is(ATOM, QUESTION);
    b.rule(NEXT_NOT_EXPRESSION).is(NOT, ATOM);
    b.rule(NEXT_EXPRESSION).is(AND, ATOM);
    b.rule(ATOM).is(b.firstOf(
      RULE_KEY,
      STRING,
      b.sequence(OPEN, FIRST_OF_EXPRESSION, CLOSE)));

    b.rule(RULE_KEY).is(b.regexp("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*+"), WHITESPACE);
    b.rule(STRING).is(b.regexp("\"[^\"]*+\""), WHITESPACE);

    b.rule(ASSIGN).is("=", WHITESPACE);
    b.rule(SEMICOLON).is(";", WHITESPACE);
    b.rule(OR).is("|", WHITESPACE);
    b.rule(NOT).is("!", WHITESPACE);
    b.rule(AND).is("&", WHITESPACE);
    b.rule(QUESTION).is("?", WHITESPACE);
    b.rule(PLUS).is("+", WHITESPACE);
    b.rule(STAR).is("*", WHITESPACE);
    b.rule(OPEN).is("(", WHITESPACE);
    b.rule(CLOSE).is(")", WHITESPACE);
    b.rule(WHITESPACE).is(b.regexp("\\s*+"));

    return b.build();
  }

}
