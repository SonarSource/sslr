/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.menu;

import com.sonar.sslr.dsl.Literal;

public class Menu {

  protected String appetizer;
  protected String mainCourse;
  protected String dessert;

  public void setAppetizer(Literal appetizer) {
    this.appetizer = appetizer.toString();
  }

  public void setMainCourse(Literal mainCourse) {
    this.mainCourse = mainCourse.toString();
  }

  public void setDessert(Literal dessert) {
    this.dessert = dessert.toString();
  }

}
