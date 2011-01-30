/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.trading;

import java.util.Map;

public class PrintPortfolio {

  private Portfolio portfolio;
  private StringBuilder output;

  public void setPortfolio(Portfolio portfolio) {
    this.portfolio = portfolio;
  }

  public void setOutput(StringBuilder output) {
    this.output = output;
  }

  public void execute() {
    Map<String, Integer> positions = portfolio.getPositions();
    for (String product : positions.keySet()) {
      output.append(positions.get(product) + " " + product);
    }
  }
}
