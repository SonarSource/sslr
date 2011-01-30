/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {

  private double totalAmount = 0;
  private Map<String, Integer> positions = new HashMap<String, Integer>();

  public Double getTotalAmount() {
    return totalAmount;
  }

  public Integer getQuantityOf(String product) {
    return positions.get(product);
  }

  public void buy(int quantitity, String product, double price) {
    totalAmount += quantitity * price;
    if ( !positions.containsKey(product)) {
      positions.put(product, 0);
    }
    positions.put(product, positions.get(product) + quantitity);
  }

  public void sell(int quantitity, String product, double price) {
    totalAmount -= quantitity * price;
    if ( !positions.containsKey(product)) {
      positions.put(product, 0);
    }
    positions.put(product, positions.get(product) - quantitity);
  }

  public Map<String, Integer> getPositions() {
    return positions;
  }
}
