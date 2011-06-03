/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import static com.sonar.sslr.dsl.DslTokenType.WORD;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.test.parser.ParserMatchers.parse;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.DslRunner;
import com.sonar.sslr.dsl.internal.DefaultDslLexer;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class StructuralSearchPatternDslTest {

  Parser<StructuralSearchPatternDsl> parser = Parser.builder(new StructuralSearchPatternDsl()).optSetLexer(new DefaultDslLexer()).build();

  @Test
  public void shouldParseExpression() {
    assertThat(parser, parse("this(*)"));
    assertThat(parser, parse("this(\"value1\", \"value2\")"));
  }

  @Test(expected = RecognitionExceptionImpl.class)
  public void shouldNotParseExpression() {
    parser.parse("this(*");
  }

  @Test
  public void shouldBuildTheStructuralSearchPattern() {
    StructuralSearchPattern pattern = new StructuralSearchPattern();
    DslRunner.builder(new StructuralSearchPatternDsl(pattern), "this(*)").build();

    assertThat(pattern.getMatcher(), is(instanceOf(ThisNodeMatcher.class)));
  }

  private class Geography extends Grammar {

    public Rule world;
    public Rule nation;
    public Rule capital;

    public Geography() {
      world.is(one2n(nation));
      nation.is(capital);
      capital.is(WORD);
    }

    @Override
    public Rule getRootRule() {
      return world;
    }

  }

}
