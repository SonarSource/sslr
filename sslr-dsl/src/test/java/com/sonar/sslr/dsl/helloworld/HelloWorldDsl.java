/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import static com.sonar.sslr.dsl.DslTokenType.LITERAL;

import com.sonar.sslr.dsl.BasicDsl;
public class HelloWorldDsl extends BasicDsl {

  public HelloWorldDsl() {
    statement.is("print", LITERAL).plug(HelloWorld.class);
  }
}
