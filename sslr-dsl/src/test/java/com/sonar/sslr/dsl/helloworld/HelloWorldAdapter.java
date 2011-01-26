/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import java.io.IOException;
import java.io.Writer;

public class HelloWorldAdapter {

  private Writer output;

  public HelloWorldAdapter(Writer output) {
    this.output = output;
  }

  public void execute() {
    try {
      output.append("hello world!");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
