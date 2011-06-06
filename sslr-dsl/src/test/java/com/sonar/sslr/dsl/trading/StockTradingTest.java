/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.Dsl;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

public class StockTradingTest {

  Portfolio portfolio = new Portfolio();
  StringBuilder output = new StringBuilder();
  Dsl.Builder builder = Dsl.builder().setGrammar(new StockTradingDsl()).inject(portfolio).inject(output);

  @Test
  public void shouldParseAllStatements() throws URISyntaxException {
    builder.withSource("buy 500 'AAPL' at 179.30").compile();
    builder.withSource("sell 500 'AAPL' at 179.30 ").compile();
    builder.withSource("print portfolio").compile();
  }

  @Test
  public void shouldBuyProduct() throws URISyntaxException {
    builder.withSource("buy 500 'AAPL' at 179.30").compile().execute();
    assertThat(portfolio.getQuantityOf("'AAPL'"), is(500));
    assertThat(portfolio.getTotalAmount(), is(500 * 179.30));
  }

  @Test
  public void shouldSellProduct() throws URISyntaxException {
    builder.withSource("sell 500 'AAPL' at 179.30").compile().execute();
    assertThat(portfolio.getQuantityOf("'AAPL'"), is( -500));
    assertThat(portfolio.getTotalAmount(), is( -500 * 179.30));
  }

  @Test
  public void shouldPrintPortfolio() throws URISyntaxException {
    builder.withSource("buy 500 'AAPL' at 179.30 \n" + "sell 250 'AAPL' at 179.30\n" + "print portfolio\n").compile().execute();
    assertThat(output.toString(), is("250 'AAPL'"));
  }
}
