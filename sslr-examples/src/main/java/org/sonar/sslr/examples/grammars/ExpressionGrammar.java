/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.sslr.examples.grammars;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

/**
 * This class demonstrates how to use {@link LexerlessGrammarBuilder} to define grammar for simple arithmetic expressions.
 */
public enum ExpressionGrammar implements GrammarRuleKey {

  WHITESPACE,

  PLUS,
  MINUS,
  DIV,
  MUL,
  NUMBER,
  VARIABLE,
  LPAR,
  RPAR,

  EXPRESSION,
  ADDITIVE_EXPRESSION,
  MULTIPLICATIVE_EXPRESSION,
  PRIMARY,
  PARENS;

  public static LexerlessGrammarBuilder createGrammarBuilder() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(WHITESPACE).is(b.commentTrivia(b.regexp("\\s*+"))).skip();

    b.rule(PLUS).is('+', WHITESPACE);
    b.rule(MINUS).is('-', WHITESPACE);
    b.rule(DIV).is('/', WHITESPACE);
    b.rule(MUL).is('*', WHITESPACE);
    b.rule(NUMBER).is(b.regexp("[0-9]++"), WHITESPACE);
    b.rule(VARIABLE).is(b.regexp("\\p{javaJavaIdentifierStart}++\\p{javaJavaIdentifierPart}*+"), WHITESPACE);
    b.rule(LPAR).is('(', WHITESPACE);
    b.rule(RPAR).is(')', WHITESPACE);

    b.rule(EXPRESSION).is(WHITESPACE, ADDITIVE_EXPRESSION, b.endOfInput());
    b.rule(ADDITIVE_EXPRESSION).is(MULTIPLICATIVE_EXPRESSION, b.zeroOrMore(b.firstOf(PLUS, MINUS), MULTIPLICATIVE_EXPRESSION));
    b.rule(MULTIPLICATIVE_EXPRESSION).is(PRIMARY, b.zeroOrMore(b.firstOf(DIV, MUL), PRIMARY)).skipIfOneChild();
    b.rule(PRIMARY).is(b.firstOf(NUMBER, PARENS, VARIABLE)).skipIfOneChild();
    b.rule(PARENS).is(LPAR, ADDITIVE_EXPRESSION, RPAR);

    b.setRootRule(EXPRESSION);

    return b;
  }

}
