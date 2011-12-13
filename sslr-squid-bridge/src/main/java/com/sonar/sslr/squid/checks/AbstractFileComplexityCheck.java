/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import org.sonar.api.utils.SonarException;
import org.sonar.check.RuleProperty;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;

public abstract class AbstractFileComplexityCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  public abstract MetricDef getComplexityMetric();

  @RuleProperty(key = "complexity")
  public int complexity;

  @Override
  public void init() {
    if (complexity <= 0) {
      throw new SonarException("[FileComplexityCheck] The complexity threshold must be set to a value greater than 0 (" + complexity
          + " given).");
    }
  }

  @Override
  public void leaveFile(AstNode astNode) {
    SourceFile sourceFile = (SourceFile) getContext().peekSourceCode();
    int fileComplexity = ChecksHelper.getRecursiveMeasureInt(sourceFile, getComplexityMetric());

    if (fileComplexity > complexity) {
      getContext().log(this, "The file is too complex ({0} while maximum allowed is set to {1}).", -1, fileComplexity, complexity);
    }
  }

}
