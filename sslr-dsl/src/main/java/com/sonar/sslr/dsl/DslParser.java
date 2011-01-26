/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.dsl;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;

class DslParser extends Parser<Grammar> {

  public DslParser(DslDefinition grammar) {
    super(grammar, new DslLexer());
  }
}
