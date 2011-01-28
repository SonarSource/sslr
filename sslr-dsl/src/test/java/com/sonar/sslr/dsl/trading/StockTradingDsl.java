/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import static com.sonar.sslr.dsl.DslTokenType.FLOAT;
import static com.sonar.sslr.dsl.DslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.Matchers.opt;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.Dsl;

public class StockTradingDsl extends Dsl {

  public Rule buy;
  public Rule sell;
  public Rule showTransactions;
  public Rule printPortfolio;

  public StockTradingDsl() {
    statement.isOr(buy, sell, showTransactions, printPortfolio);

    buy.is(opt("("), "buy", INTEGER, LITERAL, "at", FLOAT, opt(")"));
    sell.is(opt("("), "sell", INTEGER, LITERAL, "at", FLOAT, opt(")"));
    showTransactions.is("show", "transactions", LITERAL);
    printPortfolio.is("print", "portfolio");
  }
}
