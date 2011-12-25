/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.menu;

import com.sonar.sslr.dsl.bytecode.ExecutableInstruction;

public class PrintMenu implements ExecutableInstruction {

  private Menu menu;

  public PrintMenu(Menu menu) {
    this.menu = menu;
  }

  public void execute() {
    //System.out.println("Menu with " + menu.appetizer + " and " + menu.mainCourse + "and finally " + menu.dessert);
  }

}
