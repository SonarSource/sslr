/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import com.sonar.sslr.dsl.bytecode.ExecutableInstruction;

public abstract class Operation implements ExecutableInstruction {

  protected int quantitity;
  protected String product;
  protected double price;

  protected Portfolio portfolio;

  public Operation(Portfolio portfolio) {
    this.portfolio = portfolio;
  }

  public void addQuantity(Integer quantity) {
    this.quantitity = quantity;
  }

  public void addProduct(String product) {
    this.product = product;
  }

  public void addPrice(Double price) {
    this.price = price;
  }
}
