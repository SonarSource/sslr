/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public class PatternMatcher extends StructuralUnitMatcher {

  private SequenceMatcher sequenceMatcher;
  private ParentMatcher parentMatcher;

  public void setSequenceMatcher(SequenceMatcher sequenceMatcher) {
    this.sequenceMatcher = sequenceMatcher;
  }

  public void setParentMatcher(ParentMatcher parentMatcher) {
    this.parentMatcher = parentMatcher;
  }

  public AstNode match(AstNode astNode) {
    if (sequenceMatcher != null) {
      return sequenceMatcher.match(astNode);
    } else if (parentMatcher != null) {
      return parentMatcher.match(astNode);
    } else {
      throw new StructuralPatternMatcherException("Nothing has been injected into the PatternMatcher object");
    }
  }
}
