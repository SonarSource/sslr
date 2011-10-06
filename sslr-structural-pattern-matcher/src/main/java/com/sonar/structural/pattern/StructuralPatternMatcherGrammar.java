/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.dsl.DslTokenType.WORD;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.LeftRecursiveRule;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.Literal;

public class StructuralPatternMatcherGrammar extends Grammar {

  public Rule compilationUnit;
  public Rule matcher;
  public Rule thisMatcher;
  public Rule parentMatcher;
  public Rule directParentMatcher;
  public Rule indirectParentMatcher;
  public Rule childMatcher;
  public Rule indirectChildMatcher;
  public Rule ruleMatcher;
  public Rule sequenceMatcher;
  public LeftRecursiveRule beforeMatcher;
  public Rule afterMatcher;
  public Rule ruleValueList;
  public Rule tokenValue;
  public Rule rule;

  public StructuralPatternMatcherGrammar(PatternMatcher patternMatcher) {
    compilationUnit.is(or(sequenceMatcher, parentMatcher));
    parentMatcher.is(or(directParentMatcher, indirectParentMatcher));
    directParentMatcher.is(rule, "(", or(sequenceMatcher, parentMatcher), ")", opt("(", childMatcher, ")"));
    indirectParentMatcher.is(rule, "(", "(", or(sequenceMatcher, parentMatcher), ")", ")", opt("(", childMatcher, ")"));

    sequenceMatcher.is(opt(beforeMatcher), thisMatcher, opt(afterMatcher));
    beforeMatcher.is(opt(beforeMatcher), not("this"), or(rule, tokenValue));
    afterMatcher.is(or(ruleMatcher, tokenValue), opt(afterMatcher));

    thisMatcher.is("this", "(", or("*", one2n(or(rule, tokenValue), opt("or"))), ")", opt("(", childMatcher, ")"));

    childMatcher.isOr(and("(", or(indirectChildMatcher), ")"), indirectChildMatcher);
    indirectChildMatcher.is(rule, opt("(", or(childMatcher), ")"));
    indirectChildMatcher.or(tokenValue);

    ruleMatcher.is(rule, opt("(", or(childMatcher), ")"));

    compilationUnit.plug(patternMatcher);
    directParentMatcher.plug(DirectParentNodeMatcher.class);
    indirectParentMatcher.plug(IndirectParentNodeMatcher.class);
    thisMatcher.plug(ThisNodeMatcher.class);
    sequenceMatcher.plug(SequenceMatcher.class);
    beforeMatcher.plug(BeforeMatcher.class);
    afterMatcher.plug(AfterMatcher.class);
    indirectChildMatcher.plug(IndirectChildNodeMatcher.class);
    ruleMatcher.plug(RuleMatcher.class);

    tokenValue.is(LITERAL).plug(Literal.class);
    rule.is(WORD).plug(String.class);
  }

  @Override
  public Rule getRootRule() {
    return compilationUnit;
  }
}
