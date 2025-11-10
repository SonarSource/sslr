/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl.ast;

import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.MockTokenType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Arrays;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.mockito.Mockito.*;

public class AstWalkerTest {

  private final AstWalker walker = new AstWalker();
  private AstNode ast1;
  private AstNode ast11;
  private AstNode ast12;
  private AstNode ast121;
  private AstNode ast122;
  private AstNode ast13;
  private AstNode astNodeWithToken;
  private final Token token = mockToken(MockTokenType.WORD, "word");

  private final AstNodeType animal = new AstNodeType() {
  };

  private final AstNodeType dog = new AstNodeType() {
  };

  private final AstNodeType cat = new AstNodeType() {
  };

  private final AstNodeType tiger = new AstNodeType() {
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
    astNodeWithToken = new AstNode(token);

    ast1.addChild(ast11);
    ast1.addChild(ast12);
    ast1.addChild(ast13);
    ast12.addChild(ast121);
    ast12.addChild(ast122);
  }

  @Test
  public void testVisitFileAndLeaveFileCalls() {
    when(astVisitor.getAstNodeTypesToVisit()).thenReturn(new ArrayList<>());
    walker.addVisitor(astVisitor);
    walker.walkAndVisit(ast1);
    verify(astVisitor).visitFile(ast1);
    verify(astVisitor).leaveFile(ast1);
    verify(astVisitor, never()).visitNode(ast11);
  }

  @Test
  public void testVisitToken() {
    when(astAndTokenVisitor.getAstNodeTypesToVisit()).thenReturn(new ArrayList<>());
    walker.addVisitor(astAndTokenVisitor);
    walker.walkAndVisit(astNodeWithToken);
    verify(astAndTokenVisitor).visitFile(astNodeWithToken);
    verify(astAndTokenVisitor).leaveFile(astNodeWithToken);
    verify(astAndTokenVisitor).visitToken(token);
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

  @Test
  public void testAddVisitor() {
    AstWalker walker = new AstWalker();

    AstNodeType astNodeType = mock(AstNodeType.class);

    AstVisitor visitor1 = mock(AstVisitor.class);
    when(visitor1.getAstNodeTypesToVisit()).thenReturn(Arrays.asList(astNodeType));

    AstVisitor visitor2 = mock(AstVisitor.class);
    when(visitor2.getAstNodeTypesToVisit()).thenReturn(Arrays.asList(astNodeType));

    walker.addVisitor(visitor1);
    walker.addVisitor(visitor2);
  }

}
