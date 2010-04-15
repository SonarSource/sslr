/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

import static com.sonar.sslr.impl.MockTokenType.WORD;
import static com.sonar.sslr.impl.matcher.Matchers.o2n;
import static com.sonar.sslr.impl.matcher.Matchers.opt;
import static com.sonar.sslr.impl.matcher.Matchers.or;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class RuleTest {

  private RuleImpl javaClassDefinition;
  private Matcher opMatcher;

  @Before
  public void init() {
    javaClassDefinition = new RuleImpl("JavaClassDefinition");
    opMatcher = opt("implements", WORD, o2n(",", WORD));
    javaClassDefinition.is("public", or("class", "interface"), opMatcher);
  }

  @Test
  public void testEBNFNotation() {
    assertEquals("JavaClassDefinition := public (class | interface) (implements WORD (, WORD)*)?", javaClassDefinition.toEBNFNotation());
  }

  @Test
  public void testToString() {
    assertEquals("JavaClassDefinition", javaClassDefinition.toString());
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyIs() {
    javaClassDefinition = new RuleImpl("JavaClassDefinition");
    javaClassDefinition.is();
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyOr() {
    javaClassDefinition = new RuleImpl("JavaClassDefinition");
    javaClassDefinition.or();
  }

  @Test
  public void testGetParentRule() {
    assertSame(javaClassDefinition, javaClassDefinition.getRule());
    assertSame(javaClassDefinition, opMatcher.getRule());
  }

  @Test
  public void testSetParentRule() {
    RuleImpl parentRule1 = new RuleImpl("ParentRule1");
    RuleImpl parentRule2 = new RuleImpl("ParentRule2");

    RuleImpl childRule = new RuleImpl("ChildRule");

    parentRule1.is(childRule);
    assertSame(parentRule1, childRule.getParentRule());

    parentRule2.is(childRule);
    assertNull(childRule.getParentRule());
  }
}
