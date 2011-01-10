/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.Matchers.and;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;

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

  @Test
  public void testDetectLeftRecursionAndStop() throws Exception {
    AstNode node = recursiveRule.parse(tokens);
    assertThat(node.toString(), is("recursiveRule token='1' line=0 column=0 file='Dummy for unit tests'"));
  }

}
