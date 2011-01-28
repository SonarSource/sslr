/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;


/**
 * A Rule describes a grammar syntactic rule.
 * 
 * @see Grammar
 * @see <a href="http://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form">Backus�Naur Form</a>
 */
public interface Rule extends AstNodeType {

  /**
   * This method allows the define the current rule as the sequence of the matchers passed as parameters, respecting the order of the given
   * matchers. <br>
   * <br>
   * <b>Note:</b> this method can be called only once for a rule. If it is called more than once, an IllegalStateException will be thrown.
   * If the rule definition really needs to be redefine, then the {@link Rule#override(Object...)} method must be used.
   * 
   * @see Rule#override(Object...)
   * @param matchers
   *          the matchers that define the rule
   * @return the rule
   */
  public Rule is(Object... matchers);

  /**
   * This method has the same effect as {@link RuleImpl#is(Object...)}, except that it can be called more than once to redefine a rule from
   * scratch. It can be used if the rule has to be redefined later (for instance in grammar extensions).
   * 
   * @see RuleImpl#override(Object...)
   * @param matchers
   *          the matchers that define the rule
   * @return the rule
   */
  public Rule override(Object... matchers);

  public Rule or(Object... matchers);

  public Rule and(Object... matchers);

  public Rule orBefore(Object... matchers);

  public Rule isOr(Object... matchers);

  public Rule setListener(AstListener listener);

  public Rule skip();

  public Rule skipIf(AstNodeType policy);

  public Rule skipIfOneChild();

  public void mockUpperCase();

  public void mock();

  public void setAdapter(Class adapterClass);
}
