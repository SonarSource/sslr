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
package org.sonar.sslr.examples.expression;

import org.sonar.sslr.grammar.GrammarRule;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.parser.LexerlessGrammar;

/**
 * This class demonstrates how to use {@link LexerlessGrammarBuilder} to define grammar for simple arithmetic expressions.
 */
public enum ExpressionGrammarRules implements GrammarRule {

  WHITESPACE,
  END_OF_INPUT,

  PLUS,
  MINUS,
  DIV,
  MUL,
  NUMBER,
  VARIABLE,
  LPAR,
  RPAR,

  ROOT,
  EXPRESSION,
  TERM,
  FACTOR,
  PARENS;

  public static LexerlessGrammarBuilder createGrammarBuilder() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(WHITESPACE).is(b.commentTrivia(b.regexp("\\s*+")));

    b.rule(PLUS).is('+', WHITESPACE);
    b.rule(MINUS).is('-', WHITESPACE);
    b.rule(DIV).is('/', WHITESPACE);
    b.rule(MUL).is('*', WHITESPACE);
    b.rule(NUMBER).is(b.regexp("[0-9]++"), WHITESPACE);
    b.rule(VARIABLE).is(b.regexp("\\p{javaJavaIdentifierStart}++\\p{javaJavaIdentifierPart}*+"), WHITESPACE);
    b.rule(LPAR).is('(', WHITESPACE);
    b.rule(RPAR).is(')', WHITESPACE);
    b.rule(END_OF_INPUT).is(b.endOfInput());

    b.rule(ROOT).is(WHITESPACE, EXPRESSION, END_OF_INPUT);
    b.rule(EXPRESSION).is(TERM, b.zeroOrMore(b.firstOf(PLUS, MINUS), TERM));
    b.rule(TERM).is(FACTOR, b.zeroOrMore(b.firstOf(DIV, MUL), FACTOR));
    b.rule(FACTOR).is(b.firstOf(NUMBER, PARENS, VARIABLE));
    b.rule(PARENS).is(LPAR, EXPRESSION, RPAR);

    b.setRootRule(ROOT);

    return b;
  }

  public static LexerlessGrammar createGrammar() {
    return createGrammarBuilder().build();
  }

}
