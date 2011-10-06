/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeBrowser;

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
    StructuralPatternMatcher.compile("divideStmt(this('\\'escaped\\''))");
    StructuralPatternMatcher.compile("divideStmt('DIVIDE' this(*))");
    StructuralPatternMatcher.compile("divideStmt((this(myRule or anotherRule)))");
    StructuralPatternMatcher.compile("stmt((divideStmt(this(*))))");
    StructuralPatternMatcher.compile("this('value1' or 'value2')");
    StructuralPatternMatcher.compile("this(*)(child((anotherChild)))");
    StructuralPatternMatcher.compile("this(*)(('child'))");
    StructuralPatternMatcher.compile("this(*) rule(child)");
    StructuralPatternMatcher.compile("parent(this(*))('child1' child2 'child3')");
    StructuralPatternMatcher.compile("divideStmt(this(*))(ruleName)");
  }

  @Test(expected = StructuralPatternMatcherException.class)
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

    pattern = StructuralPatternMatcher.compile("this(*) nation(capital) nation");
    assertThat(pattern.isMatching(paris), is(true));
    
    pattern = StructuralPatternMatcher.compile("this(*) nation(nation) nation");
    assertThat(pattern.isMatching(paris), is(false));

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
  public void shouldMatchDirectParentWithChild() {
    pattern = StructuralPatternMatcher.compile("nation(this(*))(capital)");
    assertThat(pattern.isMatching(paris), is(true));
    pattern = StructuralPatternMatcher.compile("world((this(*)))((capital))");
    assertThat(pattern.isMatching(paris), is(true));

    pattern = StructuralPatternMatcher.compile("nation(this(*))(nation)");
    assertThat(pattern.isMatching(paris), is(false));
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
  public void shouldMatchIndirectTokenChild() {
    pattern = StructuralPatternMatcher.compile("this(*)(('Paris'))");
    assertThat(pattern.isMatching(world), is(true));
    
    pattern = StructuralPatternMatcher.compile("this(*)(('London'))");
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
