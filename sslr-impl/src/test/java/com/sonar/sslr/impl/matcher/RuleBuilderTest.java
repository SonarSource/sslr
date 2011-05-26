/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RuleBuilderTest {

  @Test(expected = IllegalStateException.class)
  public void testEmptyIs() {
    RuleBuilder javaClassDefinition = new RuleBuilder("JavaClassDefinition", false);
    javaClassDefinition.is();
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyIsOr() {
    RuleBuilder javaClassDefinition = new RuleBuilder("JavaClassDefinition", false);
    javaClassDefinition.isOr();
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyOr() {
    RuleBuilder javaClassDefinition = new RuleBuilder("JavaClassDefinition", false);
    javaClassDefinition.or();
  }

  @Test(expected = IllegalStateException.class)
  public void testCallingOrWithoutHavingCallIsFirst() {
    RuleBuilder javaClassDefinition = new RuleBuilder("JavaClassDefinition", false);
    javaClassDefinition.or("keyword");
  }

  @Test(expected = IllegalStateException.class)
  public void testMoreThanOneDefinitionForASigleRuleWithIs() {
    RuleBuilder javaClassDefinition = new RuleBuilder("JavaClassDefinition", false);
    javaClassDefinition.is("option1");
    javaClassDefinition.is("option2");
  }

  @Test(expected = IllegalStateException.class)
  public void testMoreThanOneDefinitionForASigleRuleWithIsOr() {
    RuleBuilder javaClassDefinition = new RuleBuilder("JavaClassDefinition", false);
    javaClassDefinition.is("");
    javaClassDefinition.isOr("");
  }

  @Test
  public void testIsOr() {
    RuleBuilder myRule = new RuleBuilder("MyRule", false);
    myRule.isOr("option1", "option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(or(\"option1\", \"option2\"))"));
  }

  @Test
  public void testIs() {
    RuleBuilder myRule = new RuleBuilder("MyRule", false);
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
  }

  @Test
  public void testOverride() {
    RuleBuilder myRule = new RuleBuilder("MyRule", false);
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
    myRule.override("option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option2\")"));
  }

  @Test
  public void testOr() {
    RuleBuilder myRule = new RuleBuilder("MyRule", false);
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
    myRule.or("option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(or(\"option1\", \"option2\"))"));
    myRule.or("option3", "option4");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(or(or(\"option1\", \"option2\"), and(\"option3\", \"option4\")))"));
  }

  @Test
  public void testOrBefore() {
    RuleBuilder myRule = new RuleBuilder("MyRule", false);
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
    myRule.orBefore("option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(or(\"option2\", \"option1\"))"));
  }
}
