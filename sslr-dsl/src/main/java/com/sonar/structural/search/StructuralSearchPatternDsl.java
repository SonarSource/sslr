/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.dsl.DslTokenType.WORD;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;

public class StructuralSearchPatternDsl extends Grammar {

  public Rule pattern;
  public Rule matcher;
  public Rule thisNodeMatcher;
  public Rule parentMatcher;
  public Rule directParentMatcher;
  public Rule indirectParentMatcher;
  public Rule childMatcher;
  public Rule directChildMatcher;
  public Rule indirectChildMatcher;
  public Rule sequenceMatcher;
  public Rule tokenOrRuleValueList;
  public Rule ruleValueList;
  public Rule tokenValue;
  public Rule nodeValue;
  public Rule ruleValue;

  public StructuralSearchPatternDsl(StructuralSearchPattern structuralSearchPattern) {
    pattern.is(or(sequenceMatcher, parentMatcher), EOF).plug(structuralSearchPattern);
    parentMatcher.isOr(directParentMatcher, indirectParentMatcher);
    directParentMatcher.is(ruleValue, "(", or(sequenceMatcher, parentMatcher), ")").plug(DirectParentNodeMatcher.class);
    indirectParentMatcher.is(ruleValue, "(", "(", or(sequenceMatcher, parentMatcher), ")", ")").plug(IndirectParentNodeMatcher.class);

    sequenceMatcher.is(o2n(not("this"), or(nodeValue, tokenValue)), thisNodeMatcher, o2n(or(nodeValue, tokenValue)));
    thisNodeMatcher.is("this", "(", or("*", tokenOrRuleValueList), ")", opt("(", childMatcher, ")")).plug(ThisNodeMatcher.class);

    childMatcher.isOr(directChildMatcher, indirectChildMatcher);
    directChildMatcher.is(ruleValue, opt("(", or(childMatcher), ")")).plug(DirectChildNodeMatcher.class);
    indirectChildMatcher.is("(", ruleValue, opt("(", or(childMatcher), ")"), ")").plug(IndirectChildNodeMatcher.class);

    tokenOrRuleValueList.is(one2n(or(tokenValue, nodeValue), opt(",")));
    tokenValue.is(LITERAL).plug(String.class);
    nodeValue.is(WORD).plug(String.class);
    ruleValue.is(WORD).plug(String.class);
  }

  public StructuralSearchPatternDsl() {
    this(new StructuralSearchPattern());
  }

  @Override
  public Rule getRootRule() {
    return pattern;
  }
}
