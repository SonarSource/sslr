/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.DslRunner;
import com.sonar.sslr.dsl.internal.DefaultDslLexer;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

import static com.sonar.sslr.test.parser.ParserMatchers.parse;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class StructuralSearchPatternDslTest {

  Parser<StructuralSearchPatternDsl> parser = Parser.builder(new StructuralSearchPatternDsl()).optSetLexer(new DefaultDslLexer()).build();
  AstNode astNode = GeographyDsl.geographyParser.parse("Paris London");
  StructuralSearchPattern pattern = new StructuralSearchPattern();

  @Test
  public void shouldParseExpression() {
    assertThat(parser, parse("'MOVE' this(*) 'TO' 'SEND-TO'"));
    assertThat(parser, parse("divideStmt(this(*))"));
    assertThat(parser, parse("divideStmt('DIVIDE' this(*))"));
    assertThat(parser, parse("divideStmt((this(myRule, anotherRule)))"));
    assertThat(parser, parse("stmt((divideStmt(this(*))))"));
    assertThat(parser, parse("this('value1', 'value2')"));
    assertThat(parser, parse("this(*)(child((anotherChild)))"));
  }

  @Test(expected = RecognitionExceptionImpl.class)
  public void shouldNotParseExpression() {
    parser.parse("this(*");
  }

  @Test
  public void shouldBuildTheStructuralSearchPattern() {
    DslRunner.builder(new StructuralSearchPatternDsl(pattern), "this(*)").build();

    assertThat(pattern.matcher, is(instanceOf(ThisNodeMatcher.class)));
  }

  @Test
  public void shouldMatchAnything() {
    DslRunner.builder(new StructuralSearchPatternDsl(pattern), "this(*)").build();
    assertThat(pattern.isMatching(astNode), is(true));
  }

  @Test
  public void shouldMatchRuleName() {
    DslRunner.builder(new StructuralSearchPatternDsl(pattern), "this(world)").build();
    assertThat(pattern.isMatching(astNode), is(true));
  }

  @Test
  public void shouldMatchTokenName() {
    DslRunner.builder(new StructuralSearchPatternDsl(pattern), "this('Paris')").build();
    assertThat(pattern.isMatching(astNode), is(true));
  }
}
