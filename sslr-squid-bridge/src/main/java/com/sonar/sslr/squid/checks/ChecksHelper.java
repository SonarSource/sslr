/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import org.sonar.squid.api.SourceCode;
import org.sonar.squid.measures.MetricDef;

public final class ChecksHelper {

  private ChecksHelper() {
  }

  public static int getRecursiveMeasureInt(SourceCode sourceCode, MetricDef metric) {
    int childrenValue = 0;

    if (sourceCode.getChildren() != null) {
      for (SourceCode child : sourceCode.getChildren()) {
        childrenValue += getRecursiveMeasureInt(child, metric);
      }
    }

    return sourceCode.getInt(metric) + childrenValue;
  }

}
