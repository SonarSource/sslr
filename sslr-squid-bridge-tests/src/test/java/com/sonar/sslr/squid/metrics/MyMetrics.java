/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.metrics;

import org.sonar.squid.measures.AggregationFormula;
import org.sonar.squid.measures.CalculatedMetricFormula;
import org.sonar.squid.measures.MetricDef;

public enum MyMetrics implements MetricDef {
  COMPLEXITY, LINES_OF_CODE, LINES, COMMENT_LINES, BLANK_COMMENT_LINES, COMMENTED_LINES_OF_CODE;

  public String getName() {
    return name();
  }

  public double getInitValue() {
    return 0;
  }

  public boolean isCalculatedMetric() {
    return false;
  }

  public boolean aggregateIfThereIsAlreadyAValue() {
    return false;
  }

  public boolean isThereAggregationFormula() {
    return false;
  }

  public CalculatedMetricFormula getCalculatedMetricFormula() {
    return null;
  }

  public AggregationFormula getAggregationFormula() {
    return null;
  }

}
