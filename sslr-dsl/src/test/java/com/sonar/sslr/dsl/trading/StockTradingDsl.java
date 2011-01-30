/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import static com.sonar.sslr.dsl.DslTokenType.FLOAT;
import static com.sonar.sslr.dsl.DslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.dsl.DslTokenType.WORD;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.BasicDsl;

public class StockTradingDsl extends BasicDsl {

  public Rule buy;
  public Rule sell;
  public Rule showTransactions;
  public Rule printPortfolio;
  public Rule amount;
  public Rule product;
  public Rule price;

  public StockTradingDsl() {
    statement.isOr(buy, sell, showTransactions, printPortfolio);

    buy.is("buy", amount, product, "at", price);
    sell.is("sell", amount, product, "at", price);
    showTransactions.is("show", "transactions", LITERAL);
    printPortfolio.is("print", "portfolio");

    amount.is(INTEGER);
    product.is(LITERAL);
    price.is(FLOAT);
  }
}
