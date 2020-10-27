/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.api;

import com.sonar.sslr.test.minic.MiniCGrammar;
import org.junit.Test;

import java.util.List;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static com.sonar.sslr.test.minic.MiniCParser.parseString;
import static org.fest.assertions.Assertions.assertThat;

public class AstNodeTest {

  @Test
  public void testAddChild() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    AstNode stat = new AstNode(new NodeType(), "stat", null);
    AstNode assign = new AstNode(new NodeType(), "assign", null);
    expr.addChild(stat);
    expr.addChild(assign);

    assertThat(expr.getChildren()).contains(stat, assign);
  }

  @Test
  public void testAddNullChild() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    expr.addChild(null);

    assertThat(expr.hasChildren()).isFalse();
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

    assertThat(expr.getChildren()).contains(stat, print);
  }

  @Test
  public void testAddMatcherChildWithoutChildren() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    AstNode all = new AstNode(new NodeType(true), "all", null);
    expr.addChild(all);

    assertThat(expr.getChildren().size()).isEqualTo(0);
  }

  @Test
  public void testHasChildren() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    assertThat(expr.hasChildren()).isFalse();
  }

  @Test
  public void testGetChild() {
    AstNode parent = new AstNode(new NodeType(), "parent", null);
    AstNode child1 = new AstNode(new NodeType(), "child1", null);
    AstNode child2 = new AstNode(new NodeType(), "child2", null);
    parent.addChild(child1);
    parent.addChild(child2);

    assertThat(parent.getChild(0)).isSameAs(child1);
    assertThat(parent.getChild(1)).isSameAs(child2);
  }

  @Test
  public void testGetLastToken() {
    Token lastToken = mockToken(GenericTokenType.IDENTIFIER, "LAST_TOKEN");
    AstNode parent = new AstNode(new NodeType(), "parent", lastToken);
    AstNode child1 = new AstNode(new NodeType(), "child1", null);
    AstNode child2 = new AstNode(new NodeType(), "child2", lastToken);
    parent.addChild(child1);
    parent.addChild(child2);

    assertThat(parent.getLastToken()).isSameAs(lastToken);
    assertThat(child2.getLastToken()).isSameAs(lastToken);
  }

  @Test
  public void testGetTokens() {
    Token child1Token = mockToken(GenericTokenType.IDENTIFIER, "CHILD 1");
    Token child2Token = mockToken(GenericTokenType.IDENTIFIER, "CHILD 2");
    AstNode parent = new AstNode(new NodeType(), "parent", null);
    AstNode child1 = new AstNode(new NodeType(), "child1", child1Token);
    AstNode child2 = new AstNode(new NodeType(), "child2", child2Token);
    parent.addChild(child1);
    parent.addChild(child2);

    assertThat(parent.getTokens().size()).isEqualTo(2);
    assertThat(parent.getTokens().get(0)).isSameAs(child1Token);
    assertThat(parent.getTokens().get(1)).isSameAs(child2Token);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetChildWithBadIndex() {
    AstNode parent = new AstNode(new NodeType(), "parent", mockToken(GenericTokenType.IDENTIFIER, "PI"));
    AstNode child1 = new AstNode(new NodeType(), "child1", null);
    parent.addChild(child1);
    parent.getChild(1);
  }

  @Test
  public void testNextSibling() {
    AstNode expr1 = new AstNode(new NodeType(), "expr1", null);
    AstNode expr2 = new AstNode(new NodeType(), "expr2", null);
    AstNode statement = new AstNode(new NodeType(), "statement", null);

    statement.addChild(expr1);
    statement.addChild(expr2);

    assertThat(expr1.nextSibling()).isSameAs(expr2);
    assertThat(expr2.nextSibling()).isNull();
  }

  @Test
  public void testPreviousSibling() {
    AstNode expr1 = new AstNode(new NodeType(), "expr1", null);
    AstNode expr2 = new AstNode(new NodeType(), "expr2", null);
    AstNode statement = new AstNode(new NodeType(), "statement", null);

    statement.addChild(expr1);
    statement.addChild(expr2);

    assertThat(expr1.previousSibling()).isNull();
    assertThat(expr2.previousSibling()).isSameAs(expr1);
  }

  @Test
  public void testFindFirstDirectChild() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    NodeType statRule = new NodeType();
    AstNode stat = new AstNode(statRule, "stat", null);
    AstNode identifier = new AstNode(new NodeType(), "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertThat(expr.findFirstDirectChild(statRule)).isSameAs(stat);
    NodeType anotherRule = new NodeType();
    assertThat(expr.findFirstDirectChild(anotherRule, statRule)).isSameAs(stat);
  }

  @Test
  public void testIs() {
    AstNode declarationNode = parseString("int a = 0;").getFirstChild();

    assertThat(declarationNode.is(MiniCGrammar.DEFINITION)).isTrue();
    assertThat(declarationNode.is(MiniCGrammar.COMPILATION_UNIT, MiniCGrammar.DEFINITION)).isTrue();
    assertThat(declarationNode.is(MiniCGrammar.DEFINITION, MiniCGrammar.COMPILATION_UNIT)).isTrue();
    assertThat(declarationNode.is(MiniCGrammar.COMPILATION_UNIT)).isFalse();
  }

  @Test
  public void testIsNot() {
    AstNode declarationNode = parseString("int a = 0;").getFirstChild();

    assertThat(declarationNode.isNot(MiniCGrammar.DEFINITION)).isFalse();
    assertThat(declarationNode.isNot(MiniCGrammar.COMPILATION_UNIT, MiniCGrammar.DEFINITION)).isFalse();
    assertThat(declarationNode.isNot(MiniCGrammar.DEFINITION, MiniCGrammar.COMPILATION_UNIT)).isFalse();
    assertThat(declarationNode.isNot(MiniCGrammar.COMPILATION_UNIT)).isTrue();
  }

  @Test
  public void testFindChildren() {
    AstNode fileNode = parseString("int a = 0; int myFunction() { int b = 0; { int c = 0; } }");

    List<AstNode> binVariableDeclarationNodes = fileNode.findChildren(MiniCGrammar.BIN_VARIABLE_DEFINITION);
    assertThat(binVariableDeclarationNodes.size()).isEqualTo(3);
    assertThat(binVariableDeclarationNodes.get(0).getTokenValue()).isEqualTo("a");
    assertThat(binVariableDeclarationNodes.get(1).getTokenValue()).isEqualTo("b");
    assertThat(binVariableDeclarationNodes.get(2).getTokenValue()).isEqualTo("c");

    List<AstNode> binVDeclarationNodes = fileNode.findChildren(MiniCGrammar.BIN_VARIABLE_DEFINITION, MiniCGrammar.BIN_FUNCTION_DEFINITION);
    assertThat(binVDeclarationNodes.size()).isEqualTo(4);
    assertThat(binVDeclarationNodes.get(0).getTokenValue()).isEqualTo("a");
    assertThat(binVDeclarationNodes.get(1).getTokenValue()).isEqualTo("myFunction");
    assertThat(binVDeclarationNodes.get(2).getTokenValue()).isEqualTo("b");
    assertThat(binVDeclarationNodes.get(3).getTokenValue()).isEqualTo("c");

    assertThat(fileNode.findChildren(MiniCGrammar.MULTIPLICATIVE_EXPRESSION).size()).isEqualTo(0);
  }

  @Test
  public void testFindDirectChildren() {
    AstNode fileNode = parseString("int a = 0; void myFunction() { int b = 0*3; { int c = 0; } }");

    List<AstNode> declarationNodes = fileNode.findDirectChildren(MiniCGrammar.DEFINITION);
    assertThat(declarationNodes.size()).isEqualTo(2);
    assertThat(declarationNodes.get(0).getTokenValue()).isEqualTo("int");
    assertThat(declarationNodes.get(1).getTokenValue()).isEqualTo("void");

    List<AstNode> binVDeclarationNodes = fileNode.findDirectChildren(MiniCGrammar.BIN_VARIABLE_DEFINITION,
      MiniCGrammar.BIN_FUNCTION_DEFINITION);
    assertThat(binVDeclarationNodes.size()).isEqualTo(0);
  }

  @Test
  public void testFindFirstChildAndHasChildren() {
    AstNode expr = new AstNode(new NodeType(), "expr", null);
    AstNode stat = new AstNode(new NodeType(), "stat", null);
    NodeType indentifierRule = new NodeType();
    AstNode identifier = new AstNode(indentifierRule, "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertThat(expr.findFirstChild(indentifierRule)).isSameAs(identifier);
    assertThat(expr.hasChildren(indentifierRule)).isTrue();
    NodeType anotherRule = new NodeType();
    assertThat(expr.findFirstChild(anotherRule)).isNull();
    assertThat(expr.hasChildren(anotherRule)).isFalse();
  }

  @Test
  public void testHasParents() {
    NodeType exprRule = new NodeType();
    AstNode expr = new AstNode(exprRule, "expr", null);
    AstNode stat = new AstNode(new NodeType(), "stat", null);
    AstNode identifier = new AstNode(new NodeType(), "identifier", null);
    expr.addChild(stat);
    expr.addChild(identifier);

    assertThat(identifier.hasParents(exprRule)).isTrue();
    assertThat(identifier.hasParents(new NodeType())).isFalse();
  }

  @Test
  public void testGetLastChild() {
    AstNode expr1 = new AstNode(new NodeType(), "expr1", null);
    AstNode expr2 = new AstNode(new NodeType(), "expr2", null);
    AstNode statement = new AstNode(new NodeType(), "statement", null);
    statement.addChild(expr1);
    statement.addChild(expr2);

    assertThat(statement.getLastChild()).isSameAs(expr2);
  }

  /**
   * <pre>
   * A1
   *  |__ C1
   *  |    |__ B1
   *  |__ B2
   *  |__ D1
   *  |__ B3
   * </pre>
   */
  @Test
  public void test_getDescendants() {
    NodeType a = new NodeType();
    NodeType b = new NodeType();
    NodeType c = new NodeType();
    NodeType d = new NodeType();
    NodeType e = new NodeType();
    AstNode a1 = new AstNode(a, "a1", null);
    AstNode c1 = new AstNode(c, "c1", null);
    AstNode b1 = new AstNode(b, "b1", null);
    AstNode b2 = new AstNode(b, "b2", null);
    AstNode d1 = new AstNode(d, "d1", null);
    AstNode b3 = new AstNode(b, "b3", null);
    a1.addChild(c1);
    c1.addChild(b1);
    a1.addChild(b2);
    a1.addChild(d1);
    a1.addChild(b3);

    assertThat(a1.findChildren(b, c)).containsExactly(c1, b1, b2, b3);
    assertThat(a1.findChildren(b)).containsExactly(b1, b2, b3);
    assertThat(a1.findChildren(e)).isEmpty();
    assertThat(a1.findChildren(a)).as("SSLR-249").containsExactly(a1);

    assertThat(a1.getDescendants(b, c)).containsExactly(c1, b1, b2, b3);
    assertThat(a1.getDescendants(b)).containsExactly(b1, b2, b3);
    assertThat(a1.getDescendants(e)).isEmpty();
    assertThat(a1.getDescendants(a)).as("SSLR-249").isEmpty();
  }

  private class NodeType implements AstNodeSkippingPolicy {

    private boolean skippedFromAst = false;

    public NodeType() {

    }

    public NodeType(boolean skippedFromAst) {
      this.skippedFromAst = skippedFromAst;
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
      return skippedFromAst;
    }

  }

}
