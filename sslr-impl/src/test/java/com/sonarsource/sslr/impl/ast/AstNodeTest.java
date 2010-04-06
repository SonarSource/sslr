/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl.ast;

import org.junit.Test;

import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.impl.matcher.AndMatcher;
import com.sonarsource.sslr.impl.matcher.OneToNMatcher;
import com.sonarsource.sslr.impl.matcher.RuleImpl;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AstNodeTest {

  @Test
  public void testAddChild() {
    AstNode expr = new AstNode(new RuleImpl("expr"), "expr", null);
    AstNode stat = new AstNode(new RuleImpl("stat"), "stat", null);
    AstNode assign = new AstNode(new RuleImpl("assign"), "assign", null);
    expr.addChild(stat);
    expr.addChild(assign);

    assertThat(expr.getChildren(), hasItems(stat, assign));
  }

  @Test
  public void testAddNullChild() {
    AstNode expr = new AstNode(new RuleImpl("expr"), "expr", null);
    expr.addChild(null);

    assertFalse(expr.hasChildren());
  }

  @Test
  public void testAddMatcherChild() {
    AstNode expr = new AstNode(new RuleImpl("expr"), "expr", null);
    AstNode all = new AstNode(new AndMatcher(), "all", null);
    AstNode stat = new AstNode(new RuleImpl("stat"), "stat", null);
    all.addChild(stat);
    expr.addChild(all);

    AstNode many = new AstNode(new OneToNMatcher(null), "many", null);
    AstNode print = new AstNode(new RuleImpl("print"), "print", null);
    many.addChild(print);
    expr.addChild(many);

    assertThat(expr.getChildren(), hasItems(stat, print));
  }

  @Test
  public void testAddMatcherChildWithoutChildren() {
    AstNode expr = new AstNode(new RuleImpl("expr"), "expr", null);
    AstNode all = new AstNode(new AndMatcher(), "all", null);
    expr.addChild(all);

    assertThat(expr.getChildren().size(), is(0));
  }

  @Test
  public void testHasChildren() {
    AstNode expr = new AstNode(new RuleImpl("expr"), "expr", null);
    assertFalse(expr.hasChildren());
  }

  @Test
  public void testGetNextSibling() {
    AstNode expr1 = new AstNode(new RuleImpl("expr1"), "expr1", null);
    AstNode expr2 = new AstNode(new RuleImpl("expr2"), "expr2", null);
    AstNode statement = new AstNode(new RuleImpl("statement"), "statement", null);

    statement.addChild(expr1);
    statement.addChild(expr2);

    assertThat(expr1.nextSibling(), is(expr2));
    assertThat(expr2.nextSibling(), is(nullValue()));
  }

  @Test
  public void testFindFirstDirectChild() {
    AstNode expr = new AstNode(new RuleImpl("expr"), "expr", null);
    RuleImpl statRule = new RuleImpl("stat");
    AstNode stat = new AstNode(statRule, "stat", null);
    AstNode identifier = new AstNode(new RuleImpl("identifier"), "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertThat(expr.findFirstDirectChild(statRule), is(stat));
    RuleImpl anotherRule = new RuleImpl("anotherRule");
    assertThat(expr.findFirstDirectChild(anotherRule, statRule), is(stat));
  }

  @Test
  public void testFindFirstAndHasSomewhere() {
    AstNode expr = new AstNode(new RuleImpl("expr"), "expr", null);
    AstNode stat = new AstNode(new RuleImpl("stat"), "stat", null);
    RuleImpl indentifierRule = new RuleImpl("identifier");
    AstNode identifier = new AstNode(indentifierRule, "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertThat(expr.findFirst(indentifierRule), is(identifier));
    assertTrue(expr.hasSomewhere(indentifierRule));
    RuleImpl anotherRule = new RuleImpl("anotherRule");
    assertThat(expr.findFirst(anotherRule), is(nullValue()));
    assertFalse(expr.hasSomewhere(anotherRule));
  }

  @Test
  public void testHasAmongParents() {
    RuleImpl exprRule = new RuleImpl("expr");
    AstNode expr = new AstNode(exprRule, "expr", null);
    AstNode stat = new AstNode(new RuleImpl("stat"), "stat", null);
    AstNode identifier = new AstNode(new RuleImpl("identifier"), "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertTrue(identifier.hasAmongParents(exprRule));
    assertFalse(identifier.hasAmongParents(new RuleImpl("anotherRule")));
  }
}
