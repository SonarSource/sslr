/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.DslRunner;

public class StockTradingTest {

  @Test
  public void shouldParseAllStatements() throws URISyntaxException {
    DslRunner stockTrading = DslRunner.create(new StockTradingDsl());
    stockTrading.execute("buy 500 'AAPL' at 179.30");
    stockTrading.execute("sell 500 'AAPL' at 179.30");
    stockTrading.execute("show transactions 'AAPL'");
    stockTrading.execute("print portfolio");
  }
}
