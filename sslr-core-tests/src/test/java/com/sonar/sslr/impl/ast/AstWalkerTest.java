/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.ast;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.MockTokenType;

public class AstWalkerTest {

  private final AstWalker walker = new AstWalker();
  private AstNode ast1;
  private AstNode ast11;
  private AstNode ast12;
  private AstNode ast121;
  private AstNode ast122;
  private AstNode ast13;
  private AstNode astNodeWithToken;
  private final AstNodeType animal = new AstNodeType() {

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }
  };
  private final AstNodeType dog = new AstNodeType() {

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }
  };
  private final AstNodeType cat = new AstNodeType() {

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }
  };
  private final AstNodeType tiger = new AstNodeType() {

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }
  };
  private final AstVisitor astVisitor = mock(AstVisitor.class);
  private final AstAndTokenVisitor astAndTokenVisitor = mock(AstAndTokenVisitor.class);

  @Before
  public void init() {
    ast1 = new AstNode(animal, "1", null);
    ast11 = new AstNode(dog, "11", null);
    ast12 = new AstNode(animal, "12", null);
    ast121 = new AstNode(animal, "121", null);
    ast122 = new AstNode(tiger, "122", null);
    ast13 = new AstNode(cat, "13", null);
    astNodeWithToken = new AstNode(new Token(MockTokenType.WORD, "word"));

    ast1.addChild(ast11);
    ast1.addChild(ast12);
    ast1.addChild(ast13);
    ast12.addChild(ast121);
    ast12.addChild(ast122);
  }

  @Test
  public void testVisitFileAndLeaveFileCalls() {
    when(astVisitor.getAstNodeTypesToVisit()).thenReturn(new ArrayList<AstNodeType>());
    walker.addVisitor(astVisitor);
    walker.walkAndVisit(ast1);
    verify(astVisitor).visitFile(ast1);
    verify(astVisitor).leaveFile(ast1);
    verify(astVisitor, never()).visitNode(ast11);
  }

  @Test
  public void testVisitToken() {
    when(astAndTokenVisitor.getAstNodeTypesToVisit()).thenReturn(new ArrayList<AstNodeType>());
    walker.addVisitor(astAndTokenVisitor);
    walker.walkAndVisit(astNodeWithToken);
    verify(astAndTokenVisitor).visitFile(astNodeWithToken);
    verify(astAndTokenVisitor).leaveFile(astNodeWithToken);
    verify(astAndTokenVisitor).visitToken(new Token(MockTokenType.WORD, "word"));
  }

  @Test
  public void testVisitNodeAndLeaveNodeCalls() {
    when(astVisitor.getAstNodeTypesToVisit()).thenReturn(Arrays.asList(tiger));
    walker.addVisitor(astVisitor);
    walker.walkAndVisit(ast1);
    InOrder inOrder = inOrder(astVisitor);
    inOrder.verify(astVisitor).visitNode(ast122);
    inOrder.verify(astVisitor).leaveNode(ast122);
    verify(astVisitor, never()).visitNode(ast11);
  }
}
