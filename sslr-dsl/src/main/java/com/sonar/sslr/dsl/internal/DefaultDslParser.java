/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.dsl.internal;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;

public class DefaultDslParser extends Parser<Grammar> {

  public DefaultDslParser(Grammar grammar) {
    super(grammar, new DefaultDslLexer());
  }
}
