/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.Literal;

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
  public Rule ruleValueList;
  public Rule tokenValue;
  public Rule nodeValue;
  public Rule ruleValue;

  public StructuralSearchPatternDsl(StructuralSearchPattern structuralSearchPattern) {
    pattern.is(or(sequenceMatcher, parentMatcher));
    parentMatcher.isOr(directParentMatcher, indirectParentMatcher);
    directParentMatcher.is(ruleValue, "(", or(sequenceMatcher, parentMatcher), ")");
    indirectParentMatcher.is(ruleValue, "(", "(", or(sequenceMatcher, parentMatcher), ")", ")");

    sequenceMatcher.is(o2n(not("this"), or(nodeValue, tokenValue)), thisNodeMatcher, o2n(or(nodeValue, tokenValue)));
    thisNodeMatcher.is("this", "(", or("*", one2n(or(tokenValue, nodeValue), opt(","))), ")", opt("(", childMatcher, ")"));

    childMatcher.isOr(directChildMatcher, indirectChildMatcher);
    directChildMatcher.is(ruleValue, opt("(", or(childMatcher), ")"));
    indirectChildMatcher.is("(", ruleValue, opt("(", or(childMatcher), ")"), ")");

    pattern.plug(structuralSearchPattern);
    directParentMatcher.plug(DirectParentNodeMatcher.class);
    indirectParentMatcher.plug(IndirectParentNodeMatcher.class);
    thisNodeMatcher.plug(ThisNodeMatcher.class);
    directChildMatcher.plug(DirectChildNodeMatcher.class);
    indirectChildMatcher.plug(IndirectChildNodeMatcher.class);

    tokenValue.is(LITERAL).plug(Literal.class);
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
