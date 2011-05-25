/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Standard.o2n;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleImpl;

public abstract class CommandListDsl extends Dsl {

  public Rule translationUnit = new RuleImpl("translationUnit");
  public Rule command = new RuleImpl("command");

  public CommandListDsl() {
    translationUnit.is(o2n(command), EOF);
  }

  public final Rule getRootRule() {
    return translationUnit;
  }
}
