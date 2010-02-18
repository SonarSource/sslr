/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.ast;

import org.junit.Test;

import com.sonarsource.parser.matcher.AndMatcher;
import com.sonarsource.parser.matcher.OneToNMatcher;
import com.sonarsource.parser.matcher.Rule;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class AstNodeTest {

  @Test
  public void testAddChild() {
    AstNode expr = new AstNode(new Rule("expr"), "expr", null);
    AstNode stat = new AstNode(new Rule("stat"), "stat", null);
    AstNode assign = new AstNode(new Rule("assign"), "assign", null);
    expr.addChild(stat);
    expr.addChild(assign);

    assertThat(expr.getChildren(), hasItems(stat, assign));
  }

  @Test
  public void testAddNullChild() {
    AstNode expr = new AstNode(new Rule("expr"), "expr", null);
    expr.addChild(null);

    assertFalse(expr.hasChildren());
  }

  @Test
  public void testAddMatcherChild() {
    AstNode expr = new AstNode(new Rule("expr"), "expr", null);
    AstNode all = new AstNode(new AndMatcher(), "all", null);
    AstNode stat = new AstNode(new Rule("stat"), "stat", null);
    all.addChild(stat);
    expr.addChild(all);

    AstNode many = new AstNode(new OneToNMatcher(null), "many", null);
    AstNode print = new AstNode(new Rule("print"), "print", null);
    many.addChild(print);
    expr.addChild(many);

    assertThat(expr.getChildren(), hasItems(stat, print));
  }

  @Test
  public void testAddMatcherChildWithoutChildren() {
    AstNode expr = new AstNode(new Rule("expr"), "expr", null);
    AstNode all = new AstNode(new AndMatcher(), "all", null);
    expr.addChild(all);

    assertThat(expr.getChildren().size(), is(0));
  }

  @Test
  public void testHasChildren() {
    AstNode expr = new AstNode(new Rule("expr"), "expr", null);
    assertFalse(expr.hasChildren());
  }

}
