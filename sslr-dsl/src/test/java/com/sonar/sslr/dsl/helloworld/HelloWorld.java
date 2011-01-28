/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

public class HelloWorld {

  private StringBuilder output;
  private String message;

  public HelloWorld(StringBuilder output) {
    this.output = output;
  }

  public void setLiteral(String message) {
    this.message = message;
  }

  public void execute() {
    output.append(message);
  }
}
