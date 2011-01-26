/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import java.io.Writer;

import com.sonar.sslr.dsl.DslDefinition;

public class HelloWorldDsl extends DslDefinition {

  public HelloWorldDsl(Writer output) {
    statement.is("hello", "world", "!");
    //statement.setAdapter(new HelloWorldAdapter(output));
  }
}
