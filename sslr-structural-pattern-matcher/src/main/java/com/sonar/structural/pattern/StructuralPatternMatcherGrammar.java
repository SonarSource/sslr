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
  public Rule childSequenceMatcher;
  public Rule childMatcher;
  public Rule ruleMatcher;
  public Rule sequenceMatcher;
  public LeftRecursiveRule beforeMatcher;
  public Rule afterMatcher;
  public Rule ruleValueList;
  public Rule tokenValue;
  public Rule rule;

  public StructuralPatternMatcherGrammar(StructuralPatternMatcher patternMatcher) {
    compilationUnit.is(or(sequenceMatcher, parentMatcher));

    parentMatcher.is(rule,
        or(and("(", "(", or(sequenceMatcher, parentMatcher), ")", ")"), and("(", or(sequenceMatcher, parentMatcher), ")")),
        opt("(", childSequenceMatcher, ")"));

    sequenceMatcher.is(opt(beforeMatcher), or(thisMatcher, parentMatcher), opt(afterMatcher));
    beforeMatcher.is(opt(beforeMatcher), or(and(rule, not("(")), tokenValue));
    afterMatcher.is(or(ruleMatcher, tokenValue), opt(afterMatcher));
    
    thisMatcher.is("this", "(", or("*", one2n(or(rule, tokenValue), opt("or"))), ")", opt("(", childSequenceMatcher, ")"));

    childSequenceMatcher.is(or(and("(", or(childMatcher), ")"), childMatcher), opt(opt(afterMatcher)));
    childMatcher.is(rule, opt("(", or(childSequenceMatcher), ")"));
    childMatcher.or(tokenValue);

    ruleMatcher.is(rule, opt("(", or(childSequenceMatcher), ")"));
    

    compilationUnit.plug(patternMatcher);
    parentMatcher.plug(ParentMatcher.class);
    childSequenceMatcher.plug(ChildSequenceMatcher.class);
    childMatcher.plug(ChildMatcher.class);
    
    thisMatcher.plug(ThisNodeMatcher.class);
    sequenceMatcher.plug(SequenceMatcher.class);
    beforeMatcher.plug(BeforeMatcher.class);
    afterMatcher.plug(AfterMatcher.class);
    
    ruleMatcher.plug(RuleMatcher.class);

    tokenValue.is(LITERAL).plug(Literal.class);
    rule.is(not("this"),WORD).plug(String.class);
  }

  @Override
  public Rule getRootRule() {
    return compilationUnit;
  }
}
