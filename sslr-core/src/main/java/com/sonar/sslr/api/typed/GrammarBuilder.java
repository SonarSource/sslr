/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2021 SonarSource SA
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
package com.sonar.sslr.api.typed;

import com.sonar.sslr.api.AstNode;
import org.sonar.sslr.grammar.GrammarRuleKey;

import java.util.List;

/**
 * @since 1.21
 */
public interface GrammarBuilder<T> {

  <U> NonterminalBuilder<U> nonterminal();

  <U> NonterminalBuilder<U> nonterminal(GrammarRuleKey ruleKey);

  <U> U firstOf(U... methods);

  <U> Optional<U> optional(U method);

  <U> List<U> oneOrMore(U method);

  <U> Optional<List<U>> zeroOrMore(U method);

  AstNode invokeRule(GrammarRuleKey ruleKey);

  T token(GrammarRuleKey ruleKey);

}
