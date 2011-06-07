/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeBrowser;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.dsl.Dsl;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

public class StructuralSearchPatternDslTest {

  AstNode world = GeographyDsl.geographyParser.parse("Paris London");
  AstNode france = new AstNodeBrowser(world).findFirstChild(GeographyDsl.grammar.nation).getResult();
  AstNode paris = new AstNodeBrowser(france).findFirstChild(GeographyDsl.grammar.capital).getResult();
  StructuralSearchPattern pattern = new StructuralSearchPattern();
  Dsl.Builder builder = Dsl.builder().setGrammar(new StructuralSearchPatternGrammar(pattern));

  @Test
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
  public void shouldMatchAnything() {
    builder.withSource("this(*)").compile();
    assertThat(pattern.isMatching(world), is(true));
  }

  @Test
  public void shouldMatchDirectParent() {
    builder.withSource("world(this(*))").compile();
    assertThat(pattern.isMatching(france), is(true));

    builder.withSource("world(nation(this(*)))").compile();
    assertThat(pattern.isMatching(paris), is(true));

    builder.withSource("unknownRule(this(*))").compile();
    assertThat(pattern.isMatching(france), is(false));
  }

  @Test
  public void shouldMatchIndirectParent() {
    builder.withSource("world((this(*)))").compile();
    assertThat(pattern.isMatching(paris), is(true));

    builder.withSource("world((this(*)))").compile();
    assertThat(pattern.isMatching(france), is(true));

    builder.withSource("world((this(unknownRule)))").compile();
    assertThat(pattern.isMatching(france), is(false));

    builder.withSource("unknown((this(*)))").compile();
    assertThat(pattern.isMatching(paris), is(false));
  }

  @Test
  public void shouldMatchDirectChild() {
    builder.withSource("this(*)(nation)").compile();
    assertThat(pattern.isMatching(world), is(true));

    builder.withSource("this(*)(nation(capital))").compile();
    assertThat(pattern.isMatching(world), is(true));

    builder.withSource("this(unknowRule)(nation(capital))").compile();
    assertThat(pattern.isMatching(world), is(false));

    builder.withSource("this(*)(nation(nation))").compile();
    assertThat(pattern.isMatching(world), is(false));

    builder.withSource("this(*)(nation)").compile();
    assertThat(pattern.isMatching(france), is(false));
  }

  @Test
  public void shouldMatchIndirectChild() {
    builder.withSource("this(*)((capital))").compile();
    assertThat(pattern.isMatching(world), is(true));

    builder.withSource("this(unknownRule)((capital))").compile();
    assertThat(pattern.isMatching(world), is(false));

    builder.withSource("this(*)((unknown))").compile();
    assertThat(pattern.isMatching(world), is(false));
  }

  @Test
  public void shouldMatchRuleName() {
    builder.withSource("this(world)").compile();
    assertThat(pattern.isMatching(world), is(true));

    builder.withSource("this(unknownRuleName)").compile();
    assertThat(pattern.isMatching(world), is(false));
  }

  @Test
  public void shouldMatchTokenName() {
    builder.withSource("this('Paris')").compile();
    assertThat(pattern.isMatching(world), is(true));

    builder.withSource("this('Unknown token value')").compile();
    assertThat(pattern.isMatching(world), is(false));
  }
}
