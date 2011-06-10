/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;

public class RuleDefinitionTest {

  @Test(expected = IllegalStateException.class)
  public void testEmptyIs() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.is();
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyIsOr() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.isOr();
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyOr() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.or();
  }

  @Test(expected = IllegalStateException.class)
  public void testCallingOrWithoutHavingCallIsFirst() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.or("keyword");
  }

  @Test(expected = IllegalStateException.class)
  public void testMoreThanOneDefinitionForASigleRuleWithIs() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.is("option1");
    javaClassDefinition.is("option2");
  }

  @Test(expected = IllegalStateException.class)
  public void testMoreThanOneDefinitionForASigleRuleWithIsOr() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.is("");
    javaClassDefinition.isOr("");
  }

  @Test
  public void testIsOr() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.isOr("option1", "option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(or(\"option1\", \"option2\"))"));
  }

  @Test
  public void testIs() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
  }

  @Test
  public void testOverride() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
    myRule.override("option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option2\")"));
  }

  @Test
  public void testOr() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
    myRule.or("option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(or(\"option1\", \"option2\"))"));
    myRule.or("option3", "option4");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(or(or(\"option1\", \"option2\"), and(\"option3\", \"option4\")))"));
  }

  @Test
  public void testOrBefore() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
    myRule.orBefore("option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(or(\"option2\", \"option1\"))"));
  }

  @Test
  public void testSkipFromAst() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null), is(false));

    ruleBuilder.skip();
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null), is(true));
  }

  @Test
  public void testSkipFromAstIf() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule").skipIfOneChild();

    AstNode parent = new AstNode(new Token(GenericTokenType.IDENTIFIER, "parent"));
    AstNode child1 = new AstNode(new Token(GenericTokenType.IDENTIFIER, "child1"));
    AstNode child2 = new AstNode(new Token(GenericTokenType.IDENTIFIER, "child2"));
    parent.addChild(child1);
    parent.addChild(child2);
    child1.addChild(child2);

    assertThat(ruleBuilder.hasToBeSkippedFromAst(parent), is(false));
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child2), is(false));
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child1), is(true));
  }
}
