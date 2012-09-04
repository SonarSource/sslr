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
package com.sonar.sslr.api;

/**
 * A Rule describes a context free grammar syntactic rule.
 *
 * @see Grammar
 */
public interface Rule extends AstNodeType {

  /**
   * This method allows to provide the definition of a context-free grammar rule. <br>
   * <br>
   * <b>Note:</b> this method can be called only once for a rule. If it is called more than once, an IllegalStateException will be thrown.
   * If the rule definition really needs to be redefine, then the {@link Rule#override(Object...)} method must be used.
   *
   * @param matchers
   *          the matchers that define the rule
   * @return this rule
   */
  Rule is(Object... matchers);

  /**
   * This method has the same effect as {@link RuleImpl#is(Object...)}, except that it can be called more than once to redefine a rule from
   * scratch. It can be used if the rule has to be redefined later (for instance in a grammar extension).
   *
   * @param matchers
   *          the matchers that define the rule
   * @return this rule
   */
  Rule override(Object... matchers);

  /**
   * Remove this node from the AST and attached its children directly to its parent
   */
  void skip();

  /**
   * Remove this node from the AST according to a provided policy
   */
  void skipIf(AstNodeSkippingPolicy policy);

  /**
   * Remove this node from the AST if it has exactly 1 child
   */
  void skipIfOneChild();

  /**
   * Utility method used for unit testing in order to dynamically replace the definition of the rule to match as soon as a token whose value
   * equals the name of the rule is encountered
   */
  void mock();

  /**
   * A rule should be flagged as being a "Recovery" rule if it's responsibility is to consume
   * some bad tokens in order to recover from a parsing error.
   *
   * In such case, all {@link RecognitionExceptionListener} injected into the {@link com.sonar.sslr.impl.Parser} are automatically
   * notified.
   *
   * @see RecognitionExceptionListener
   */
  void recoveryRule();

}
