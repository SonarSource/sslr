/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.Literal;

import static com.sonar.sslr.dsl.DslTokenType.LITERAL;

public class HelloWorldDsl extends Grammar {

  public Rule helloWorld;
  public Rule message;

  public HelloWorldDsl() {
    helloWorld.is("print", message).plug(HelloWorld.class);
    message.is(LITERAL).plug(Literal.class);
  }

  @Override
  public Rule getRootRule() {
    return helloWorld;
  }
}
