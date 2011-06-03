/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

public class Sell extends Operation {

  public Sell(Portfolio portfolio) {
    super(portfolio);
  }

  public void execute() {
    portfolio.sell(quantitity, product, price);
  }
}
