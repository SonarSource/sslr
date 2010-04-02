/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.ast;

import org.junit.Test;

import com.sonarsource.parser.matcher.AndMatcher;
import com.sonarsource.parser.matcher.OneToNMatcher;
import com.sonarsource.parser.matcher.Rule;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

  @Test
  public void testGetNextSibling() {
    AstNode expr1 = new AstNode(new Rule("expr1"), "expr1", null);
    AstNode expr2 = new AstNode(new Rule("expr2"), "expr2", null);
    AstNode statement = new AstNode(new Rule("statement"), "statement", null);

    statement.addChild(expr1);
    statement.addChild(expr2);

    assertThat(expr1.getNextSibling(), is(expr2));
    assertThat(expr2.getNextSibling(), is(nullValue()));
  }

  @Test
  public void testFindFirstDirectChild() {
    AstNode expr = new AstNode(new Rule("expr"), "expr", null);
    Rule statRule = new Rule("stat");
    AstNode stat = new AstNode(statRule, "stat", null);
    AstNode identifier = new AstNode(new Rule("identifier"), "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertThat(expr.findFirstDirectChild(statRule), is(stat));
    Rule anotherRule = new Rule("anotherRule");
    assertThat(expr.findFirstDirectChild(anotherRule, statRule), is(stat));
  }

  @Test
  public void testFindFirstAndHasSomewhere() {
    AstNode expr = new AstNode(new Rule("expr"), "expr", null);
    AstNode stat = new AstNode(new Rule("stat"), "stat", null);
    Rule indentifierRule = new Rule("identifier");
    AstNode identifier = new AstNode(indentifierRule, "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertThat(expr.findFirst(indentifierRule), is(identifier));
    assertTrue(expr.hasSomewhere(indentifierRule));
    Rule anotherRule = new Rule("anotherRule");
    assertThat(expr.findFirst(anotherRule), is(nullValue()));
    assertFalse(expr.hasSomewhere(anotherRule));
  }

  @Test
  public void testHasAmongParents() {
    Rule exprRule = new Rule("expr");
    AstNode expr = new AstNode(exprRule, "expr", null);
    AstNode stat = new AstNode(new Rule("stat"), "stat", null);
    AstNode identifier = new AstNode(new Rule("identifier"), "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertTrue(identifier.hasAmongParents(exprRule));
    assertFalse(identifier.hasAmongParents(new Rule("anotherRule")));
  }
}
