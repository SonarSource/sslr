/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Standard.o2n;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleBuilder;

public abstract class CommandListDsl extends Dsl {

  public Rule translationUnit = new RuleBuilder("translationUnit", false);
  public Rule command = new RuleBuilder("command", false);

  public CommandListDsl() {
    translationUnit.is(o2n(command), EOF);
  }

  public final Rule getRootRule() {
    return translationUnit;
  }
}
