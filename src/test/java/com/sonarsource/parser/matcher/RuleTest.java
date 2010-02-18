/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Before;
import org.junit.Test;

import static com.sonarsource.parser.MockTokenType.WORD;
import static com.sonarsource.parser.matcher.Matchers.o2n;
import static com.sonarsource.parser.matcher.Matchers.opt;
import static com.sonarsource.parser.matcher.Matchers.or;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class RuleTest {

  private Rule javaClassDefinition;
  private Matcher opMatcher;

  @Before
  public void init() {
    javaClassDefinition = new Rule("JavaClassDefinition");
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
    javaClassDefinition = new Rule("JavaClassDefinition");
    javaClassDefinition.is();
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyOr() {
    javaClassDefinition = new Rule("JavaClassDefinition");
    javaClassDefinition.or();
  }

  @Test
  public void testGetParentRule() {
    assertSame(javaClassDefinition, javaClassDefinition.getRule());
    assertSame(javaClassDefinition, opMatcher.getRule());
  }

  @Test
  public void testSetParentRule() {
    Rule parentRule1 = new Rule("ParentRule1");
    Rule parentRule2 = new Rule("ParentRule2");

    Rule childRule = new Rule("ChildRule");

    parentRule1.is(childRule);
    assertSame(parentRule1, childRule.getParentRule());

    parentRule2.is(childRule);
    assertNull(childRule.getParentRule());
  }
}
