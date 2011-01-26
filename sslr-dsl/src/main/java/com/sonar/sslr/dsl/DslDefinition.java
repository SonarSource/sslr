/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.Matchers.o2n;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.GrammarRuleLifeCycleManager;
import com.sonar.sslr.impl.matcher.RuleImpl;

public abstract class DslDefinition implements Grammar {

  protected Rule myDsl = new RuleImpl("myDsl");
  protected Rule statement = new RuleImpl("statement");

  public DslDefinition() {
    GrammarRuleLifeCycleManager.initializeRuleFields(this, this.getClass());

    myDsl.is(o2n(statement), EOF);
  }

  public Rule getRootRule() {
    return myDsl;
  }
}
