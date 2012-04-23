/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
   * Experimental
   */
  void plug(Object adapter);

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
