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
    DslRunner.create(new StockTradingDsl(), "buy 500 'AAPL' at 179.30");
    DslRunner.create(new StockTradingDsl(), "sell 500 'AAPL' at 179.30");
    DslRunner.create(new StockTradingDsl(), "show transactions 'AAPL'");
    DslRunner.create(new StockTradingDsl(), "print portfolio");
  }
}
