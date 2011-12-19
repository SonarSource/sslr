/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.dsl.Dsl;

public final class StructuralPatternMatcher extends StructuralUnitMatcher {

  private SequenceMatcher sequenceMatcher;
  private ParentMatcher parentMatcher;

  private StructuralPatternMatcher() {
  }

  public static final StructuralPatternMatcher compile(String structuralPattern) {
    StructuralPatternMatcher pattern = new StructuralPatternMatcher();
    try {
      Dsl.builder().setGrammar(new StructuralPatternMatcherGrammar(pattern)).withSource(structuralPattern).compile();
    } catch (RecognitionException e) {
      throw new StructuralPatternMatcherException("The following structural pattern is incorrect : " + structuralPattern
          + System.getProperty("line.separator") + e.getMessage());
    }
    return pattern;
  }

  public void setSequenceMatcher(SequenceMatcher sequenceMatcher) {
    this.sequenceMatcher = sequenceMatcher;
  }

  public void setParentMatcher(ParentMatcher parentMatcher) {
    this.parentMatcher = parentMatcher;
  }

  @Override
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
