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

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

import static org.sonar.sslr.examples.grammars.ExpressionGrammar.EXPRESSION;

/**
 * This class demonstrates how to use {@link LexerlessGrammarBuilder} to extend another grammar.
 */
public enum ExtendedExpressionGrammar implements GrammarRuleKey {

  FUNCTION;

  public static LexerlessGrammarBuilder createGrammarBuilder(LexerlessGrammarBuilder expressionGrammarRules) {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.createBasedOn(expressionGrammarRules);

    b.rule(ExpressionGrammar.PLUS).override("plus", ExpressionGrammar.WHITESPACE);
    b.rule(ExpressionGrammar.MINUS).override("minus", ExpressionGrammar.WHITESPACE);
    b.rule(ExpressionGrammar.DIV).override("div", ExpressionGrammar.WHITESPACE);
    b.rule(ExpressionGrammar.MUL).override("mul", ExpressionGrammar.WHITESPACE);

    b.rule(FUNCTION).is("fun()", ExpressionGrammar.WHITESPACE);
    b.rule(ExpressionGrammar.PRIMARY).override(b.firstOf(
        ExpressionGrammar.NUMBER,
        ExpressionGrammar.PARENS,
        FUNCTION,
        ExpressionGrammar.VARIABLE));

    b.setRootRule(EXPRESSION);

    return b;
  }

}
