/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package com.sonar.sslr.api;

/**
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface Rule extends AstNodeType {

  /**
   * Allows to provide definition of a grammar rule.
   * <p>
   * <b>Note:</b> this method can be called only once for a rule. If it is called more than once, an IllegalStateException will be thrown.
   *
   * @param e expression of grammar that defines this rule
   * @return this (for method chaining)
   * @throws IllegalStateException if definition has been already done
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.GrammarRuleBuilder#is(Object)} instead.
   */
  @Deprecated
  Rule is(Object... e);

  /**
   * Allows to override definition of a grammar rule.
   * <p>
   * This method has the same effect as {@link #is(Object)}, except that it can be called more than once to redefine a rule from scratch.
   *
   * @param e expression of grammar that defines this rule
   * @return this (for method chaining)
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.GrammarRuleBuilder#override(Object)} instead.
   */
  @Deprecated
  Rule override(Object... e);

  /**
   * Indicates that grammar rule should not lead to creation of AST node - its children should be attached directly to its parent.
   *
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.GrammarRuleBuilder#skip()} instead.
   */
  @Deprecated
  void skip();

  /**
   * Defines policy of creation of AST node for this rule.
   *
   * @deprecated in 1.19
   */
  @Deprecated
  void skipIf(AstNodeSkippingPolicy policy);

  /**
   * Indicates that grammar rule should not lead to creation of AST node if it has exactly one child.
   *
   * @deprecated in 1.19, use {@link org.sonar.sslr.grammar.GrammarRuleBuilder#skipIfOneChild()} instead.
   */
  @Deprecated
  void skipIfOneChild();

  /**
   * Utility method used for unit testing in order to dynamically replace the definition of the rule to match as soon as a token whose value
   * equals the name of the rule is encountered.
   *
   * @deprecated in 1.18, use {@link #override(Object...)} instead.
   */
  @Deprecated
  void mock();

  /**
   * @deprecated in 1.19, no difference between usual grammar rule and "recovery rule" - both will be presented in AST and so can be handled via AST visitor.
   */
  @Deprecated
  void recoveryRule();

}
