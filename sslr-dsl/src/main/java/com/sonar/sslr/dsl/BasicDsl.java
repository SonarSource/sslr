/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.EOL;
import static com.sonar.sslr.impl.matcher.Matchers.o2n;
import static com.sonar.sslr.impl.matcher.Matchers.opt;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleImpl;

public abstract class BasicDsl extends Dsl {

  protected Rule myDsl = new RuleImpl("myDsl");
  protected Rule statement = new RuleImpl("statement");

  public BasicDsl() {
    myDsl.is(o2n(statement, opt(EOL)), EOF);
  }

  public Rule getRootRule() {
    return myDsl;
  }
}
