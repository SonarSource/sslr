/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import com.sonar.sslr.dsl.adapter.ExecutableAdapter;

public abstract class Operation implements ExecutableAdapter {

  protected int quantitity;
  protected String product;
  protected double price;

  protected Portfolio portfolio;

  public void setPortfolio(Portfolio portfolio) {
    this.portfolio = portfolio;
  }

  public void setQuantity(Integer quantity) {
    this.quantitity = quantity;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public void setPrice(Double price) {
    this.price = price;
  }
}
