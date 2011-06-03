/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

public class StructuralSearchPatternDsl extends Grammar {

  public Rule pattern;
  public Rule thisNode;
  public Rule tokenValueList;
  public Rule tokenValue;

  public StructuralSearchPatternDsl(StructuralSearchPattern structuralSearchPattern) {
    pattern.is(thisNode, EOF).plug(structuralSearchPattern);
    thisNode.is("this", "(", or("*", tokenValueList), ")").plug(ThisNodeMatcher.class);
    tokenValueList.is(one2n(tokenValue, opt(",")));
    tokenValue.is(LITERAL).plug(String.class);
  }

  public StructuralSearchPatternDsl() {
    this(new StructuralSearchPattern());
  }

  @Override
  public Rule getRootRule() {
    return pattern;
  }
}
