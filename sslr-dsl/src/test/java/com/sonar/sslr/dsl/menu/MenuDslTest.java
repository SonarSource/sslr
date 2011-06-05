/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.menu;

import org.junit.Test;

import com.sonar.sslr.dsl.Dsl;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

public class MenuDslTest {

  @Test
  public void shouldParse() {
    Dsl.builder(new MenuGrammar(new Menu()), "menu is main course  'King Prawns' dessert 'Ice Cream' ").compile();
  }

  @Test
  public void shouldFeedMenuObject() {
    Menu menu = new Menu();
    Dsl.builder(new MenuGrammar(menu), "menu is main course  'King Prawns' dessert 'Ice Cream' ").compile();
    assertThat(menu.mainCourse, is("King Prawns"));
    assertThat(menu.dessert, is("Ice Cream"));
  }

  @Test
  public void shouldExecuteDsl() {
    Menu menu = new Menu();
    Dsl.builder(new MenuGrammar(menu), "menu is main course  'King Prawns' dessert 'Ice Cream' print menu").compile().execute();
  }

}
