/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.menu;

import com.sonar.sslr.dsl.bytecode.ExecutableInstruction;

public class OrderMenu implements ExecutableInstruction {

  private Menu menu;

  public OrderMenu(Menu menu) {
    this.menu = menu;
  }

  public void execute() {
    // do something
  }

}
