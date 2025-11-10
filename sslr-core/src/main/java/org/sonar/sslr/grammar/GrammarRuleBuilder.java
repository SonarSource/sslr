/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
package org.sonar.sslr.grammar;

/**
 * This interface contains methods used to describe rule of grammar.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 *
 * @since 1.18
 * @see LexerlessGrammarBuilder#rule(GrammarRuleKey)
 * @see LexerfulGrammarBuilder#rule(GrammarRuleKey)
 */
public interface GrammarRuleBuilder {

  /**
   * Allows to provide definition of a grammar rule.
   * <p>
   * <b>Note:</b> this method can be called only once for a rule. If it is called more than once, an GrammarException will be thrown.
   *
   * @param e  expression of grammar
   * @return this (for method chaining)
   * @throws GrammarException if definition has been already done
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  GrammarRuleBuilder is(Object e);

  /**
   * Convenience method equivalent to calling {@code is(grammarBuilder.sequence(e, rest))}.
   *
   * @param e  expression of grammar
   * @param rest  rest of expressions
   * @return this (for method chaining)
   * @throws GrammarException if definition has been already done
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   * @see #is(Object)
   */
  GrammarRuleBuilder is(Object e, Object... rest);

  /**
   * Allows to override definition of a grammar rule.
   * <p>
   * This method has the same effect as {@link #is(Object)}, except that it can be called more than once to redefine a rule from scratch.
   *
   * @param e  expression of grammar
   * @throws IllegalArgumentException if given argument is not a parsing expression
   * @return this (for method chaining)
   */
  GrammarRuleBuilder override(Object e);

  /**
   * Convenience method equivalent to calling {@code override(grammarBuilder.sequence(e, rest))}.
   *
   * @param e  expression of grammar
   * @param rest  rest of expressions
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   * @return this (for method chaining)
   * @see #override(Object)
   */
  GrammarRuleBuilder override(Object e, Object... rest);

  /**
   * Indicates that grammar rule should not lead to creation of AST node - its children should be attached directly to its parent.
   */
  void skip();

  /**
   * Indicates that grammar rule should not lead to creation of AST node if it has exactly one child.
   */
  void skipIfOneChild();

}
