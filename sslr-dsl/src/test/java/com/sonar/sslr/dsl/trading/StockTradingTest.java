/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.Dsl;

public class StockTradingTest {

  Portfolio portfolio = new Portfolio();
  StringBuilder output = new StringBuilder();

  @Test
  public void shouldParseAllStatements() throws URISyntaxException {
    Dsl.builder(new StockTradingDsl(), "buy 500 'AAPL' at 179.30").inject(portfolio).compile();
    Dsl.builder(new StockTradingDsl(), "sell 500 'AAPL' at 179.30 ").inject(portfolio).compile();
    Dsl.builder(new StockTradingDsl(), "print portfolio").inject(portfolio).inject(output).compile();
  }

  @Test
  public void shouldBuyProduct() throws URISyntaxException {
    Dsl.builder(new StockTradingDsl(), "buy 500 'AAPL' at 179.30").inject(portfolio).compile().execute();
    assertThat(portfolio.getQuantityOf("'AAPL'"), is(500));
    assertThat(portfolio.getTotalAmount(), is(500 * 179.30));
  }

  @Test
  public void shouldSellProduct() throws URISyntaxException {
    Dsl.builder(new StockTradingDsl(), "sell 500 'AAPL' at 179.30").inject(portfolio).compile().execute();
    assertThat(portfolio.getQuantityOf("'AAPL'"), is( -500));
    assertThat(portfolio.getTotalAmount(), is( -500 * 179.30));
  }

  @Test
  public void shouldPrintPortfolio() throws URISyntaxException {
    Dsl.builder(new StockTradingDsl(), "buy 500 'AAPL' at 179.30 \n" + "sell 250 'AAPL' at 179.30\n" + "print portfolio\n")
        .inject(portfolio).inject(output).compile().execute();
    assertThat(output.toString(), is("250 'AAPL'"));
  }
}
