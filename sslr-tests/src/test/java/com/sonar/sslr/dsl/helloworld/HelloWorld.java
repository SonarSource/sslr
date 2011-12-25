/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import com.sonar.sslr.dsl.Literal;
import com.sonar.sslr.dsl.bytecode.ExecutableInstruction;

public class HelloWorld implements ExecutableInstruction {

  private StringBuilder output = new StringBuilder();
  private String message;

  public HelloWorld(StringBuilder output) {
    this.output = output;
  }

  public void addMessage(Literal message) {
    this.message = message.toString();
  }

  public void execute() {
    output.append(message);
  }
}
