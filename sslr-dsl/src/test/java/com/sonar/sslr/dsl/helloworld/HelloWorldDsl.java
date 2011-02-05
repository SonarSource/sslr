/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import static com.sonar.sslr.dsl.DslTokenType.LITERAL;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.CommandListDsl;

public class HelloWorldDsl extends CommandListDsl {

  public Rule message;

  public HelloWorldDsl() {
    command.is("print", LITERAL).plug(HelloWorld.class);
  }
}
