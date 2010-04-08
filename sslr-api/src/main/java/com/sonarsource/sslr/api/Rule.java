/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.api;

/**
 * A Rule describes a grammar syntactic rule.
 * 
 * @see Grammar
 * @see <a href="http://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form">Backus–Naur Form</a>
 */
public interface Rule extends AstNodeType {

  public Rule is(Object... matchers);

  public Rule or(Object... matchers);

  public Rule skip();

  public void mock();
}
