/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.Dsl;

public class StructuralPatternMatcher {

  private PatternMatcher matcher = new PatternMatcher();

  private StructuralPatternMatcher() {
  };

  public static final StructuralPatternMatcher compile(String structuralSearchPatternDsl) {
    StructuralPatternMatcher pattern = new StructuralPatternMatcher();
    Dsl.builder().setGrammar(new StructuralPatternMatcherGrammar(pattern.matcher)).withSource(structuralSearchPatternDsl).compile();
    return pattern;
  }

  public boolean isMatching(AstNode astNode) {
    return matcher.match(astNode) != null;
  }
}
