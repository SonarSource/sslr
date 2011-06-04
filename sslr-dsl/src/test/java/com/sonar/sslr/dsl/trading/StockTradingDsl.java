/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.dsl.DefaultDslTokenType.DOUBLE;
import static com.sonar.sslr.dsl.DefaultDslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DefaultDslTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

public class StockTradingDsl extends Grammar {

  public Rule translationUnit;
  public Rule command;
  public Rule buy;
  public Rule sell;
  public Rule printPortfolio;
  public Rule quantity;
  public Rule product;
  public Rule price;

  public StockTradingDsl() {
    translationUnit.is(o2n(command), EOF);
    command.isOr(buy, sell, printPortfolio);

    buy.is("buy", quantity, product, "at", price).plug(Buy.class);
    sell.is("sell", quantity, product, "at", price).plug(Sell.class);
    printPortfolio.is("print", "portfolio").plug(PrintPortfolio.class);

    quantity.is(INTEGER).plug(Integer.class);
    product.is(LITERAL).plug(String.class);
    price.is(DOUBLE).plug(Double.class);
  }

  @Override
  public Rule getRootRule() {
    return translationUnit;
  }
}
