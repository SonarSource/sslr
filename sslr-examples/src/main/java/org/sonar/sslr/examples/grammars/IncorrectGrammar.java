/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2020 SonarSource SA
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

import com.sonar.sslr.api.Grammar;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

/**
 * This class demonstrates how SSLR detects various mistakes in grammars.
 */
public enum IncorrectGrammar implements GrammarRuleKey {

  A, B;

  public static Grammar undefinedRule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    b.rule(A);
    return b.build();
  }

  public static Grammar referenceToUndefinedRule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    b.rule(A).is(B);
    return b.build();
  }

  public static Grammar ruleDefinedTwice() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    b.rule(A).is("foo");
    b.rule(A).is("bar");
    return b.build();
  }

  public static Grammar incorrectRegularExpression() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    b.rule(A).is(b.regexp("*"));
    return b.build();
  }

  public static Grammar infiniteZeroOrMore() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    b.rule(A).is(b.zeroOrMore(b.optional("foo")));
    return b.build();
  }

  public static Grammar infiniteOneOrMore() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    b.rule(A).is(b.oneOrMore(b.optional("foo")));
    return b.build();
  }

}
