/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.parser.ast;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

public class AstWalkerTest {

  private AstWalker walker = new AstWalker();
  private AstNode ast1;
  private AstNode ast11;
  private AstNode ast12;
  private AstNode ast121;
  private AstNode ast122;
  private AstNode ast13;
  private AstNodeType animal = new AstNodeType() {
  };
  private AstNodeType dog = new AstNodeType() {
  };
  private AstNodeType cat = new AstNodeType() {
  };
  private AstNodeType tiger = new AstNodeType() {
  };
  private AstVisitor visitor = mock(AstVisitor.class);

  @Before
  public void init() {
    ast1 = new AstNode(animal, "1", null);
    ast11 = new AstNode(dog, "11", null);
    ast12 = new AstNode(animal, "12", null);
    ast121 = new AstNode(animal, "121", null);
    ast122 = new AstNode(tiger, "122", null);
    ast13 = new AstNode(cat, "13", null);

    ast1.addChild(ast11);
    ast1.addChild(ast12);
    ast1.addChild(ast13);
    ast12.addChild(ast121);
    ast12.addChild(ast122);
  }

  @Test
  public void testVisitFileAndLeaveFileCalls() {
    when(visitor.getAstNodeTypes()).thenReturn(new ArrayList<AstNodeType>());
    walker.addVisitor(visitor);
    walker.walkAndVisit(ast1);
    verify(visitor).visitFile(ast1);
    verify(visitor).leaveFile(ast1);
    verify(visitor, never()).visitNode(ast11);
  }
  
  @Test
  public void testVisitNodeAndLeaveNodeCalls() {
    when(visitor.getAstNodeTypes()).thenReturn(Arrays.asList(tiger));
    walker.addVisitor(visitor);
    walker.walkAndVisit(ast1);
    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor).visitNode(ast122);
    inOrder.verify(visitor).leaveNode(ast122);
    verify(visitor, never()).visitNode(ast11);
  }
}
