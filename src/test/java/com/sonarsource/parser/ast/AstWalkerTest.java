/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
