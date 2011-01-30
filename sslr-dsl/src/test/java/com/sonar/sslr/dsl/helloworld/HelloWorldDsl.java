/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import static com.sonar.sslr.dsl.DefaultDslTokenType.LITERAL;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.BasicDsl;
public class HelloWorldDsl extends BasicDsl {
  
  public Rule message;

  public HelloWorldDsl() {
    statement.is("print", message).plug(HelloWorld.class);
    message.is(LITERAL);
  }
}
