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

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.DIV;
import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.EXPRESSION;
import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.MINUS;
import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.MUL;
import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.NUMBER;
import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.PARENS;
import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.PLUS;
import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.PRIMARY;
import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.VARIABLE;
import static org.sonar.sslr.examples.expression.ExpressionGrammarRules.WHITESPACE;

/**
 * This class demonstrates how to use {@link LexerlessGrammarBuilder} to extend another grammar.
 */
public enum ExpressionGrammarExtensionRules implements GrammarRuleKey {

  FUNCTION;

  public static LexerlessGrammarBuilder createGrammarBuilder(LexerlessGrammarBuilder expressionGrammarRules) {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.createBasedOn(expressionGrammarRules);

    b.rule(PLUS).override("plus", WHITESPACE);
    b.rule(MINUS).override("minus", WHITESPACE);
    b.rule(DIV).override("div", WHITESPACE);
    b.rule(MUL).override("mul", WHITESPACE);

    b.rule(FUNCTION).is("fun()", WHITESPACE);
    b.rule(PRIMARY).override(b.firstOf(NUMBER, PARENS, FUNCTION, VARIABLE)).skipIfOneChild();

    b.setRootRule(EXPRESSION);

    return b;
  }

}
