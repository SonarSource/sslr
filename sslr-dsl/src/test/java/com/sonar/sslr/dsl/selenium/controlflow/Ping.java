/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.selenium.controlflow;

public class Ping {

  private StringBuilder output;

  public void setOutput(StringBuilder output) {
    this.output = output;
  }

  public void execute() {
    output.append("ping");
  }

}
