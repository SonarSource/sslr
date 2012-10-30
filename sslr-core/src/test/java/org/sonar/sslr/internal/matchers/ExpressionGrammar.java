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
package org.sonar.sslr.internal.matchers;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Rule;
import org.sonar.sslr.matchers.LexerlessGrammar;

import static org.sonar.sslr.matchers.Matchers.endOfInput;
import static org.sonar.sslr.matchers.Matchers.firstOf;
import static org.sonar.sslr.matchers.Matchers.regexp;
import static org.sonar.sslr.matchers.Matchers.token;
import static org.sonar.sslr.matchers.Matchers.zeroOrMore;

public class ExpressionGrammar extends LexerlessGrammar {

  Rule whitespace;

  Rule plus;
  Rule minus;
  Rule div;
  Rule mul;
  Rule number;
  Rule variable;
  Rule lpar;
  Rule rpar;

  Rule root;
  Rule expression;
  Rule term;
  Rule factor;
  Rule parens;

  public ExpressionGrammar() {
    whitespace.is(token(GenericTokenType.COMMENT, regexp("\\s*+"))).skip();

    plus.is(token(GenericTokenType.LITERAL, '+'), whitespace);
    minus.is(token(GenericTokenType.LITERAL, '-'), whitespace);
    div.is(token(GenericTokenType.LITERAL, '/'), whitespace);
    mul.is(token(GenericTokenType.LITERAL, '*'), whitespace);
    number.is(token(GenericTokenType.CONSTANT, regexp("[0-9]++")), whitespace);
    variable.is(token(GenericTokenType.IDENTIFIER, regexp("\\p{javaJavaIdentifierStart}++\\p{javaJavaIdentifierPart}*+")), whitespace);
    lpar.is(token(GenericTokenType.LITERAL, '('), whitespace);
    rpar.is(token(GenericTokenType.LITERAL, ')'), whitespace);

    // If in part of grammar below we will replace
    // plus, minus, div, mul, lpar and rpar by punctuators '+', '-', '/', '*', '(' and ')' respectively,
    // number by GenericTokenType.CONSTANT, variable by GenericTokenType.IDENTIFIER
    // and remove space
    // then it will look exactly as it was with lexer:
    root.is(whitespace, expression, token(GenericTokenType.EOF, endOfInput()));
    expression.is(term, zeroOrMore(firstOf(plus, minus), term));
    term.is(factor, zeroOrMore(firstOf(div, mul), factor));
    factor.is(firstOf(number, parens, variable));
    parens.is(lpar, expression, rpar);
  }

  @Override
  public Rule getRootRule() {
    return root;
  }

}
