/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

public class Buy extends Operation {

  public Buy(Portfolio portfolio) {
    super(portfolio);
  }

  public void execute() {
    portfolio.buy(quantitity, product, price);
  }
}
