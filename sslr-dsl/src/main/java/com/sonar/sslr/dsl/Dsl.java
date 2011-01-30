/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.GrammarRuleLifeCycleManager;

public abstract class Dsl implements Grammar {

  public Dsl() {
    GrammarRuleLifeCycleManager.initializeRuleFields(this, this.getClass());
  }
}
