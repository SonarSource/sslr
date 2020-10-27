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
 * While for performance reasons preferable to write greedy rules instead of recursive,
 * impossible to avoid recursion at all.
 * Depth of recursion not limited by Java stack size (i.e. can't lead to {@link StackOverflowError})
 * and limited only by amount of available memory.
 */
public enum RecursiveGrammar implements GrammarRuleKey {

  S;

  public static Grammar create() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(S).is("(", b.optional(S), ")");

    return b.build();
  }

}
