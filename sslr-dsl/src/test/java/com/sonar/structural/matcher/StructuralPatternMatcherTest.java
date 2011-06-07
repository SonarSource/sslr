/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.matcher;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeBrowser;
import com.sonar.sslr.api.RecognitionException;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

public class StructuralPatternMatcherTest {

  AstNode world = GeographyDsl.geographyParser.parse("Paris London Madrid");
  AstNode france = new AstNodeBrowser(world).findFirstChild(GeographyDsl.grammar.nation).getResult();
  AstNode paris = new AstNodeBrowser(france).findFirstChild(GeographyDsl.grammar.capital).getResult();
  AstNode london = france.nextSibling().getFirstChild();
  AstNode madrid = france.nextSibling().nextSibling().getFirstChild();
  StructuralPatternMatcher pattern;

  @Test
  public void shouldParseExpression() {
    StructuralPatternMatcher.compile("'MOVE' this(*) 'TO' 'SEND-TO'");
    StructuralPatternMatcher.compile("divideStmt(this(*))");
    StructuralPatternMatcher.compile("divideStmt('DIVIDE' this(*))");
    StructuralPatternMatcher.compile("divideStmt((this(myRule or anotherRule)))");
    StructuralPatternMatcher.compile("stmt((divideStmt(this(*))))");
    StructuralPatternMatcher.compile("this('value1' or 'value2')");
    StructuralPatternMatcher.compile("this(*)(child((anotherChild)))");
  }

  @Test(expected = RecognitionException.class)
  public void shouldNotParseExpression() {
    StructuralPatternMatcher.compile("this(*");
  }

  @Test
  public void shouldMatchAnything() {
    pattern = StructuralPatternMatcher.compile("this(*)");
    assertThat(pattern.isMatching(world), is(true));
  }

  @Test
  public void shouldMatchNextTokens() {
    pattern = StructuralPatternMatcher.compile("this(*) 'London'");
    assertThat(pattern.isMatching(paris), is(true));

    pattern = StructuralPatternMatcher.compile("this(*) 'London' 'Madrid'");
    assertThat(pattern.isMatching(paris), is(true));

    pattern = StructuralPatternMatcher.compile("this(*) 'Unknown Token Value'");
    assertThat(pattern.isMatching(paris), is(false));

    pattern = StructuralPatternMatcher.compile("this(*) 'London' 'Unknown Token Value'");
    assertThat(pattern.isMatching(paris), is(false));
  }

  @Test
  public void shouldMatchPreviousTokens() {
    pattern = StructuralPatternMatcher.compile("'London' this(*)");
    assertThat(pattern.isMatching(madrid), is(true));

    pattern = StructuralPatternMatcher.compile("'Paris' 'London' this(*)");
    assertThat(pattern.isMatching(madrid), is(true));

    pattern = StructuralPatternMatcher.compile("'Unknown Token Value' this(*)");
    assertThat(pattern.isMatching(madrid), is(false));

    pattern = StructuralPatternMatcher.compile("'Unknown Token Value' 'London' this(*)");
    assertThat(pattern.isMatching(madrid), is(false));
  }

  @Test
  public void shouldMatchPreviousNodes() {
    pattern = StructuralPatternMatcher.compile("nation this(*)");
    assertThat(pattern.isMatching(madrid), is(true));

    pattern = StructuralPatternMatcher.compile("nation nation this(*)");
    assertThat(pattern.isMatching(madrid), is(true));

    pattern = StructuralPatternMatcher.compile("capital nation this('Madrid')");
    assertThat(pattern.isMatching(madrid), is(true));

    pattern = StructuralPatternMatcher.compile("'Paris' nation this(*)");
    assertThat(pattern.isMatching(madrid), is(true));

    pattern = StructuralPatternMatcher.compile("unknowRuleName nation this(*)");
    assertThat(pattern.isMatching(madrid), is(false));
  }

  @Test
  public void shouldMatchNextNode() {
    pattern = StructuralPatternMatcher.compile("this(*) nation");
    assertThat(pattern.isMatching(paris), is(true));

    pattern = StructuralPatternMatcher.compile("this(*) nation nation");
    assertThat(pattern.isMatching(paris), is(true));

    pattern = StructuralPatternMatcher.compile("this(*) nation capital");
    assertThat(pattern.isMatching(paris), is(true));

    pattern = StructuralPatternMatcher.compile("this(*) unknowRuleName");
    assertThat(pattern.isMatching(paris), is(false));

    pattern = StructuralPatternMatcher.compile("this(*) nation unknowRuleName");
    assertThat(pattern.isMatching(paris), is(false));
  }

  @Test
  public void shouldMatchDirectParent() {
    pattern = StructuralPatternMatcher.compile("world(this(*))");
    assertThat(pattern.isMatching(france), is(true));

    pattern = StructuralPatternMatcher.compile("world(nation(this(*)))");
    assertThat(pattern.isMatching(paris), is(true));

    pattern = StructuralPatternMatcher.compile("unknownRule(this(*))");
    assertThat(pattern.isMatching(france), is(false));
  }

  @Test
  public void shouldMatchIndirectParent() {
    pattern = StructuralPatternMatcher.compile("world((this(*)))");
    assertThat(pattern.isMatching(paris), is(true));

    pattern = StructuralPatternMatcher.compile("world((this(*)))");
    assertThat(pattern.isMatching(france), is(true));

    pattern = StructuralPatternMatcher.compile("world((this(unknownRule)))");
    assertThat(pattern.isMatching(france), is(false));

    pattern = StructuralPatternMatcher.compile("unknown((this(*)))");
    assertThat(pattern.isMatching(paris), is(false));
  }

  @Test
  public void shouldMatchDirectChild() {
    pattern = StructuralPatternMatcher.compile("this(*)(nation)");
    assertThat(pattern.isMatching(world), is(true));

    pattern = StructuralPatternMatcher.compile("this(*)(nation(capital))");
    assertThat(pattern.isMatching(world), is(true));

    pattern = StructuralPatternMatcher.compile("this(unknowRule)(nation(capital))");
    assertThat(pattern.isMatching(world), is(false));

    pattern = StructuralPatternMatcher.compile("this(*)(nation(nation))");
    assertThat(pattern.isMatching(world), is(false));

    pattern = StructuralPatternMatcher.compile("this(*)(nation)");
    assertThat(pattern.isMatching(france), is(false));
  }

  @Test
  public void shouldMatchIndirectChild() {
    pattern = StructuralPatternMatcher.compile("this(*)((capital))");
    assertThat(pattern.isMatching(world), is(true));

    pattern = StructuralPatternMatcher.compile("this(unknownRule)((capital))");
    assertThat(pattern.isMatching(world), is(false));

    pattern = StructuralPatternMatcher.compile("this(*)((unknown))");
    assertThat(pattern.isMatching(world), is(false));
  }

  @Test
  public void shouldMatchRuleName() {
    pattern = StructuralPatternMatcher.compile("this(world)");
    assertThat(pattern.isMatching(world), is(true));

    pattern = StructuralPatternMatcher.compile("this(unknownRuleName)");
    assertThat(pattern.isMatching(world), is(false));
  }

  @Test
  public void shouldMatchTokenName() {
    pattern = StructuralPatternMatcher.compile("this('Paris')");
    assertThat(pattern.isMatching(world), is(true));

    pattern = StructuralPatternMatcher.compile("this('Unknown token value')");
    assertThat(pattern.isMatching(world), is(false));
  }

  @Test
  public void shouldMatchCompositeExamples() {
    pattern = StructuralPatternMatcher.compile("'Paris' this('London') 'Madrid'");
    assertThat(pattern.isMatching(london), is(true));

    pattern = StructuralPatternMatcher.compile("this(world)");
    assertThat(pattern.isMatching(world), is(true));

    pattern = StructuralPatternMatcher.compile("'Paris' this('London') 'Madrid'");
    assertThat(pattern.isMatching(madrid), is(false));
  }
}
