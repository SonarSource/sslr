/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.sslr.internal.matchers;

import com.sonar.sslr.api.Rule;
import org.sonar.sslr.parser.LexerlessGrammar;

import static org.sonar.sslr.parser.GrammarOperators.commentTrivia;
import static org.sonar.sslr.parser.GrammarOperators.endOfInput;
import static org.sonar.sslr.parser.GrammarOperators.firstOf;
import static org.sonar.sslr.parser.GrammarOperators.regexp;
import static org.sonar.sslr.parser.GrammarOperators.zeroOrMore;

public class ExpressionGrammar extends LexerlessGrammar {

  Rule whitespace;
  Rule endOfInput;

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
    whitespace.is(commentTrivia(regexp("\\s*+"))).skip();

    plus.is('+', whitespace);
    minus.is('-', whitespace);
    div.is('/', whitespace);
    mul.is('*', whitespace);
    number.is(regexp("[0-9]++"), whitespace);
    variable.is(regexp("\\p{javaJavaIdentifierStart}++\\p{javaJavaIdentifierPart}*+"), whitespace);
    lpar.is('(', whitespace);
    rpar.is(')', whitespace);
    endOfInput.is(endOfInput());

    // If in part of grammar below we will replace
    // plus, minus, div, mul, lpar and rpar by punctuators '+', '-', '/', '*', '(' and ')' respectively,
    // number by GenericTokenType.CONSTANT, variable by GenericTokenType.IDENTIFIER
    // and remove space
    // then it will look exactly as it was with lexer:
    root.is(whitespace, expression, endOfInput);
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
