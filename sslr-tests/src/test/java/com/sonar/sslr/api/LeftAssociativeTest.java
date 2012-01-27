/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.sonar.sslr.impl.ast.LeftAssociative;

public class LeftAssociativeTest {

  @Test
  public void testOperatorAndOperand() {
    AstNodeType operator = mock(AstNodeType.class);
    AstNode operand = mock(AstNode.class);

    LeftAssociative.OperatorAndOperand operatorAndOperand = new LeftAssociative.OperatorAndOperand(operator, operand);

    assertThat(operatorAndOperand.getOperator(), is(operator));
    assertThat(operatorAndOperand.getOperand(), is(operand));
  }

  @Test
  public void testIterator() {
    AstNode operand1 = mock(AstNode.class);

    AstNode operator1 = mock(AstNode.class);
    AstNodeType operator1Type = mock(AstNodeType.class);
    when(operator1.getType()).thenReturn(operator1Type);

    AstNode operand2 = mock(AstNode.class);

    AstNode operator2 = mock(AstNode.class);
    AstNodeType operator2Type = mock(AstNodeType.class);
    when(operator2.getType()).thenReturn(operator2Type);

    AstNode operand3 = mock(AstNode.class);

    AstNode node = mock(AstNode.class);
    when(node.getChild(0)).thenReturn(operand1);
    when(node.getChild(1)).thenReturn(operator1);
    when(node.getChild(2)).thenReturn(operand2);
    when(node.getChild(3)).thenReturn(operator2);
    when(node.getChild(4)).thenReturn(operand3);
    when(node.getNumberOfChildren()).thenReturn(5);

    LeftAssociative.Iterator iterator = new LeftAssociative.Iterator(node);

    assertThat(iterator.hasNext(), is(true));
    LeftAssociative.OperatorAndOperand operandAndOperator = iterator.next();
    assertThat(operandAndOperator.getOperator(), is(operator1Type));
    assertThat(operandAndOperator.getOperand(), is(operand2));
    assertThat(iterator.hasNext(), is(true));
    operandAndOperator = iterator.next();
    assertThat(operandAndOperator.getOperator(), is(operator2Type));
    assertThat(operandAndOperator.getOperand(), is(operand3));
    assertThat(iterator.hasNext(), is(false));
  }

}
