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

  Menu menu = new Menu();
  Dsl.Builder builder = Dsl.builder().setGrammar(new MenuGrammar(menu));

  @Test
  public void shouldParse() {
    builder.withSource("menu is main course  'King Prawns' dessert 'Ice Cream' ").compile();
  }

  @Test
  public void shouldFeedMenuObject() {
    builder.withSource("menu is main course  'King Prawns' dessert 'Ice Cream' ").compile();
    assertThat(menu.mainCourse, is("King Prawns"));
    assertThat(menu.dessert, is("Ice Cream"));
  }

  @Test
  public void shouldExecuteDsl() {
    builder.withSource("menu is main course  'King Prawns' dessert 'Ice Cream' print menu").compile().execute();
  }

}
