/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal.controlflow;

import com.sonar.sslr.dsl.adapter.ExecutableAdapter;

public class Ping implements ExecutableAdapter {

  private StringBuilder output;

  public Ping(StringBuilder output) {
    this.output = output;
  }

  public void execute() {
    output.append("ping");
  }

}
