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
