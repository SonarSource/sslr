/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import static com.sonar.sslr.dsl.DefaultDslTokenType.DOUBLE;
import static com.sonar.sslr.dsl.DefaultDslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DefaultDslTokenType.LITERAL;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.BasicDsl;

public class StockTradingDsl extends BasicDsl {

  public Rule buy;
  public Rule sell;
  public Rule printPortfolio;
  public Rule quantity;
  public Rule product;
  public Rule price;

  public StockTradingDsl() {
    statement.isOr(buy, sell, printPortfolio);

    buy.is("buy", quantity, product, "at", price).plug(Buy.class);
    sell.is("sell", quantity, product, "at", price).plug(Sell.class);
    printPortfolio.is("print", "portfolio").plug(PrintPortfolio.class);

    quantity.is(INTEGER);
    product.is(LITERAL);
    price.is(DOUBLE);
  }
}
