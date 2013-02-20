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
package org.sonar.sslr.examples.recursion;

import com.sonar.sslr.api.Grammar;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

public enum LeftRecursiveGrammar implements GrammarRuleKey {

  A, B, C, T1, T2, T3, S1, S2;

  public static Grammar create() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    // Immediate left recursion
    b.rule(A).is(b.firstOf(
        b.sequence(A, T1),
        T1));
    b.rule(T1).is("t1");

    // Indirect left recursion
    b.rule(B).is(b.firstOf(
        b.sequence(C, T2),
        S1));
    b.rule(C).is(b.firstOf(
        b.sequence(B, T3),
        S2));
    b.rule(T2).is("t1");
    b.rule(S1).is("s1");
    b.rule(T3).is("t2");
    b.rule(S2).is("s2");

    return b.build();
  }

}
