/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.menu;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.Literal;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.dsl.DefaultDslTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;

public class MenuGrammar extends Grammar {

  public Rule compilationUnit;
  public Rule menu;
  public Rule appetizer;
  public Rule mainCourse;
  public Rule dessert;
  public Rule choice;
  public Rule printMenu;
  public Rule orderMenu;

  public MenuGrammar(Menu menuAdapter) {
    compilationUnit.is(menu, opt(printMenu), opt(orderMenu), EOF);
    menu.is("menu", "is", opt(appetizer), mainCourse, dessert).plug(menuAdapter);
    appetizer.is("appetizer", choice);
    mainCourse.is("main", "course", choice);
    dessert.is("dessert", choice);

    choice.is(LITERAL).plug(Literal.class);

    printMenu.is("print", "menu").plug(new PrintMenu(menuAdapter));
    orderMenu.is("order", "menu").plug(new OrderMenu(menuAdapter));
  }

  public Rule getRootRule() {
    return compilationUnit;
  }
}
