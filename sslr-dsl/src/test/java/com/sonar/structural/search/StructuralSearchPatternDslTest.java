/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import org.junit.Ignore;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.dsl.Dsl;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class StructuralSearchPatternDslTest {

  AstNode astNode = GeographyDsl.geographyParser.parse("Paris London");
  StructuralSearchPattern pattern = new StructuralSearchPattern();
  Dsl.Builder builder = Dsl.builder().setGrammar(new StructuralSearchPatternDsl(pattern));

  @Test
  @Ignore
  public void shouldParseExpression() {
    builder.withSource("'MOVE' this(*) 'TO' 'SEND-TO'").compile();
    builder.withSource("divideStmt(this(*))").compile();
    builder.withSource("divideStmt('DIVIDE' this(*))").compile();
    builder.withSource("divideStmt((this(myRule, anotherRule)))").compile();
    builder.withSource("stmt((divideStmt(this(*))))").compile();
    builder.withSource("this('value1', 'value2')").compile();
    builder.withSource("this(*)(child((anotherChild)))").compile();
  }

  @Test(expected = RecognitionException.class)
  public void shouldNotParseExpression() {
    builder.withSource("this(*").compile();
  }

  @Test
  public void shouldBuildTheStructuralSearchPattern() {
    builder.withSource("this(*)").compile();
    assertThat(pattern.matcher, is(instanceOf(ThisNodeMatcher.class)));
  }

  @Test
  public void shouldMatchAnything() {
    builder.withSource("this(*)").compile();
    assertThat(pattern.isMatching(astNode), is(true));
  }

  @Test
  public void shouldMatchRuleName() {
    builder.withSource("this(world)").compile();
    assertThat(pattern.isMatching(astNode), is(true));
  }

  @Test
  public void shouldMatchTokenName() {
    builder.withSource("this('Paris')").compile();
    assertThat(pattern.isMatching(astNode), is(true));
  }
}
