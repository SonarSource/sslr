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
package org.sonar.sslr.grammar;

/**
 * This interface contains methods used to describe rule of grammar.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 *
 * @since 1.18
 */
public interface GrammarRuleBuilder {

  /**
   * Allows to provide definition of a grammar rule.
   * <p>
   * <b>Note:</b> this method can be called only once for a rule. If it is called more than once, an GrammarException will be thrown.
   *
   * @param e  grammar expression
   * @return this (for method chaining)
   * @throws GrammarException if definition has been already done
   */
  GrammarRuleBuilder is(Object e);

  /**
   * Convenience method equivalent to calling {@code is(grammarBuilder.sequence(e1, others))}.
   *
   * @return this (for method chaining)
   * @throws GrammarException if definition has been already done
   * @see #is(Object)
   */
  GrammarRuleBuilder is(Object e1, Object... others);

  /**
   * Allows to override definition of a grammar rule.
   * <p>
   * This method has the same effect as {@link #is(Object...)}, except that it can be called more than once to redefine a rule from scratch.
   *
   * @param e  grammar expression
   * @return this (for method chaining)
   */
  GrammarRuleBuilder override(Object e);

  /**
   * Convenience method equivalent to calling {@code override(grammarBuilder.sequence(e1, others))}.
   *
   * @return this (for method chaining)
   * @see #override(Object)
   */
  GrammarRuleBuilder override(Object e1, Object... others);

  /**
   * Indicates that grammar rule should not lead to creation of AST node - its children should be attached directly to its parent.
   */
  void skip();

  /**
   * Indicates that grammar rule should not lead to creation of AST node if it has exactly one child.
   */
  void skipIfOneChild();

  /**
   * Indicates that grammar rule is a "recovery" rule, i.e. it's able to consume some bad input in order to recover from a parse error.
   */
  void recoveryRule();

}
