/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

public class Sell {

  private int quantitity;
  private String product;
  private double price;

  private Portfolio portfolio;

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

  public void execute() {
    portfolio.sell(quantitity, product, price);
  }
}
