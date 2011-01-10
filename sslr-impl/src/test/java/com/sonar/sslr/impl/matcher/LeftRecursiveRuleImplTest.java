/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.Matchers.and;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.LeftRecursionDetectedException;

public class LeftRecursiveRuleImplTest {

  private RuleImpl recursiveRule;
  private List<Token> tokens;

  @Before
  public void init() {
    recursiveRule = new LeftRecursiveRuleImpl("recursiveRule");
    recursiveRule.isOr(and(recursiveRule, "+", "1"), "1");
    tokens = Lists.newArrayList();
    tokens.add(new Token(GenericTokenType.LITERAL, "1"));
    tokens.add(new Token(GenericTokenType.LITERAL, "+"));
    tokens.add(new Token(GenericTokenType.LITERAL, "1"));
    tokens.add(new Token(GenericTokenType.LITERAL, "+"));
    tokens.add(new Token(GenericTokenType.LITERAL, "1"));
  }

  @Test(expected = LeftRecursionDetectedException.class)
  public void testDetectLeftRecursion() throws Exception {
    recursiveRule.parse(tokens);
  }

}
