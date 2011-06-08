/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;

public class AstNodeTest {

  @Test
  public void testAddChild() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    AstNode stat = new AstNode(new NodeType(), "stat", null);
    AstNode assign = new AstNode(new NodeType(), "assign", null);
    expr.addChild(stat);
    expr.addChild(assign);

    assertThat(expr.getChildren(), hasItems(stat, assign));
  }

  @Test
  public void testAddNullChild() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    expr.addChild(null);

    assertFalse(expr.hasChildren());
  }

  @Test
  public void testAddChildWhichMustBeSkippedFromAst() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    AstNode all = new AstNode(new NodeType(true), "all", null);
    AstNode stat = new AstNode(new NodeType(), "stat", null);
    all.addChild(stat);
    expr.addChild(all);

    AstNode many = new AstNode(new NodeType(true), "many", null);
    AstNode print = new AstNode(new NodeType(), "print", null);
    many.addChild(print);
    expr.addChild(many);

    assertThat(expr.getChildren(), hasItems(stat, print));
  }

  @Test
  public void testAddMatcherChildWithoutChildren() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    AstNode all = new AstNode(new NodeType(true), "all", null);
    expr.addChild(all);

    assertThat(expr.getChildren().size(), is(0));
  }

  @Test
  public void testHasChildren() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    assertFalse(expr.hasChildren());
  }

  @Test
  public void testGetChild() {
    AstNode parent = new AstNode(new NodeType(), "parent", null);
    AstNode child1 = new AstNode(new NodeType(), "child1", null);
    AstNode child2 = new AstNode(new NodeType(), "child2", null);
    parent.addChild(child1);
    parent.addChild(child2);

    assertThat(parent.getChild(0), is(child1));
    assertThat(parent.getChild(1), is(child2));
  }

  @Test
  public void testGetLastToken() {
    Token lastToken = new Token(GenericTokenType.IDENTIFIER, "LAST_TOKEN");
    AstNode parent = new AstNode(new NodeType(), "parent", null);
    AstNode child1 = new AstNode(new NodeType(), "child1", null);
    AstNode child2 = new AstNode(new NodeType(), "child2", lastToken);
    parent.addChild(child1);
    parent.addChild(child2);

    assertThat(parent.getLastToken(), is(lastToken));
    assertThat(child2.getLastToken(), is(lastToken));
  }

  @Test
  public void testGetTokens() {
    Token child1Token = new Token(GenericTokenType.IDENTIFIER, "CHILD 1");
    Token child2Token = new Token(GenericTokenType.IDENTIFIER, "CHILD 2");
    AstNode parent = new AstNode(new NodeType(), "parent", null);
    AstNode child1 = new AstNode(new NodeType(), "child1", child1Token);
    AstNode child2 = new AstNode(new NodeType(), "child2", child2Token);
    parent.addChild(child1);
    parent.addChild(child2);

    assertThat(parent.getTokens().size(), is(2));
    assertThat(parent.getTokens().get(0), is(child1Token));
    assertThat(parent.getTokens().get(1), is(child2Token));
  }

  @Test
  public void dumpSourceCode() {
    Token child1Token = new Token(GenericTokenType.IDENTIFIER, "CHILD 1", 1, 2);
    Token child2Token = new Token(GenericTokenType.IDENTIFIER, "CHILD 2", 1, 12);
    Token child3Token = new Token(GenericTokenType.IDENTIFIER, "CHILD 3", 2, 0);
    AstNode parent = new AstNode(new NodeType(), "parent", null);
    AstNode child1 = new AstNode(new NodeType(), "child1", child1Token);
    AstNode child2 = new AstNode(new NodeType(), "child2", child2Token);
    AstNode child3 = new AstNode(new NodeType(), "child3", child3Token);
    parent.addChild(child1);
    parent.addChild(child2);
    parent.addChild(child3);

    assertThat(parent.dumpSourceCode(), is("  CHILD 1   CHILD 2\nCHILD 3"));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetChildWithBadIndex() {
    AstNode parent = new AstNode(new NodeType(), "parent", new Token(GenericTokenType.IDENTIFIER, "PI"));
    AstNode child1 = new AstNode(new NodeType(), "child1", null);
    parent.addChild(child1);
    parent.getChild(1);
  }

  @Test
  public void testStartListening() {
    AstNode node = new AstNode(new NodeType(), "child1", null);
    AstListener listener = mock(AstListener.class);
    Object listenersOutput = mock(Object.class);
    node.setAstNodeListener(listener);
    node.startListening(listenersOutput);
    verify(listener).startListening(node, listenersOutput);
  }

  @Test
  public void testStartListeningWithoutListener() {
    AstNode node = new AstNode(new NodeType(), "child1", null);
    Object listenersOutput = mock(Object.class);
    node.startListening(listenersOutput);
  }

  @Test
  public void testStopListening() {
    AstNode node = new AstNode(new NodeType(), "child1", null);
    AstListener listener = mock(AstListener.class);
    Object listenersOutput = mock(Object.class);
    node.setAstNodeListener(listener);
    node.stopListening(listenersOutput);
    verify(listener).stopListening(node, listenersOutput);
  }

  @Test
  public void testStopListeningWithoutListener() {
    AstNode node = new AstNode(new NodeType(), "child1", null);
    Object listenersOutput = mock(Object.class);
    node.stopListening(listenersOutput);
  }

  @Test
  public void testNextSibling() {
    AstNode expr1 = new AstNode(new NodeType(), "expr1", null);
    AstNode expr2 = new AstNode(new NodeType(), "expr2", null);
    AstNode statement = new AstNode(new NodeType(), "statement", null);

    statement.addChild(expr1);
    statement.addChild(expr2);

    assertThat(expr1.nextSibling(), is(expr2));
    assertThat(expr2.nextSibling(), is(nullValue()));
  }

  @Test
  public void testPreviousSibling() {
    AstNode expr1 = new AstNode(new NodeType(), "expr1", null);
    AstNode expr2 = new AstNode(new NodeType(), "expr2", null);
    AstNode statement = new AstNode(new NodeType(), "statement", null);

    statement.addChild(expr1);
    statement.addChild(expr2);

    assertThat(expr1.previousSibling(), is(nullValue()));
    assertThat(expr2.previousSibling(), is(expr1));
  }

  @Test
  public void testFindFirstDirectChild() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    NodeType statRule = new NodeType();
    AstNode stat = new AstNode(statRule, "stat", null);
    AstNode identifier = new AstNode(new NodeType(), "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertThat(expr.findFirstDirectChild(statRule), is(stat));
    NodeType anotherRule = new NodeType();
    assertThat(expr.findFirstDirectChild(anotherRule, statRule), is(stat));
  }

  @Test
  public void testFindChildren() {
    NodeType idType = new NodeType();
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    AstNode stat = new AstNode(new NodeType(), "stat", null);
    AstNode id1 = new AstNode(idType, "id1", null);
    AstNode id2 = new AstNode(idType, "id2", null);

    expr.addChild(stat);
    expr.addChild(id1);
    stat.addChild(id2);

    List<AstNode> identifiers = expr.findChildren(idType);

    assertThat(identifiers.size(), is(2));
    assertThat(identifiers, hasItems(id1, id2));
  }

  @Test
  public void testFindFirstChildAndHasChildren() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    AstNode stat = new AstNode(new NodeType(), "stat", null);
    NodeType indentifierRule = new NodeType();
    AstNode identifier = new AstNode(indentifierRule, "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertThat(expr.findFirstChild(indentifierRule), is(identifier));
    assertTrue(expr.hasChildren(indentifierRule));
    NodeType anotherRule = new NodeType();
    assertThat(expr.findFirstChild(anotherRule), is(nullValue()));
    assertFalse(expr.hasChildren(anotherRule));
  }

  @Test
  public void testHasParents() {
    NodeType exprRule = new NodeType();
    AstNode expr = new AstNode(exprRule, "expr", null);
    AstNode stat = new AstNode(new NodeType(), "stat", null);
    AstNode identifier = new AstNode(new NodeType(), "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertTrue(identifier.hasParents(exprRule));
    assertFalse(identifier.hasParents(new NodeType()));
  }

  @Test
  public void testGetLastChild() {
    AstNode expr1 = new AstNode(new NodeType(), "expr1", null);
    AstNode expr2 = new AstNode(new NodeType(), "expr2", null);
    AstNode statement = new AstNode(new NodeType(), "statement", null);
    statement.addChild(expr1);
    statement.addChild(expr2);

    assertThat(statement.getLastChild(), is(expr2));
  }

  private class NodeType implements AstNodeSkippingPolicy {

    private boolean skippedFromAst = false;

    public NodeType() {

    }

    public NodeType(boolean skippedFromAst) {
      this.skippedFromAst = skippedFromAst;
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return skippedFromAst;
    }

  }
}