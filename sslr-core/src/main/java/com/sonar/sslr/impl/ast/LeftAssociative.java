/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.impl.ast;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

/**
 * Utility class which helps to deal with left associative rules, which typically have the form:
 *
 * <pre>
 * higherPrecendenceExpressionRule.is(
 *     lowerPrecedenceExpressionRule,
 *     o2n(highPrecedenceOperator, lowerPrecedenceExpressionRule)
 *     ).skipIfOneChild();
 * </pre>
 *
 * Let's take as an example a simple calculator with the binary subtraction as only operator:
 *
 * <pre>
 * primaryExpression.is(INTEGER_LITERAL);
 *
 * substractionExpression.is(
 *     primaryExpression,
 *     o2n(&quot;-&quot;, primaryExpression)
 *     ).skipIfOneChild();
 * </pre>
 *
 * The interpretation of the subtraction is made easy by this class, and is done as follows:
 *
 * <pre>
 * public int evaluate(AstNode node) {
 *   if (node.getType() == primaryExpression) {
 *     return evaluatePrimaryExpression(node);
 *   } else if (node.getType() == substractionExpression) {
 *     return evaluateSubtractionExpression(node);
 *   } else {
 *     throw new IllegalArgumentException(&quot;Unsupported node type &quot; + node.getType());
 *   }
 * }
 *
 * private int evaluatePrimaryExpression(AstNode node) {
 *   return Integer.parseInt(node.getTokenOriginalValue());
 * }
 *
 * private int evaluateSubtractionExpression(AstNode node) {
 *   int accumulator = evaluate(node.getChild(0));
 *
 *   for (LeftAssociative.OperatorAndOperand operatorAndOperand : new LeftAssociative.Iterator(node)) {
 *     // If several binary operators have the same precedence, use operatorAndOperand.getOperator()
 *     accumulator -= evaluate(operatorAndOperand.getOperand());
 *   }
 *
 *   return accumulator;
 * }
 * </pre>
 *
 * @deprecated in 1.15
 */
@Deprecated
public final class LeftAssociative {

  private LeftAssociative() {
  }

  public static class OperatorAndOperand {

    private final AstNodeType operator;
    private final AstNode operand;

    public OperatorAndOperand(AstNodeType operator, AstNode operand) {
      this.operator = operator;
      this.operand = operand;
    }

    public AstNodeType getOperator() {
      return operator;
    }

    public AstNode getOperand() {
      return operand;
    }

  }

  public static class Iterator implements java.util.Iterator<OperatorAndOperand>, java.lang.Iterable<OperatorAndOperand> {

    private final AstNode leftAssociativeNode;
    private int index;

    public Iterator(AstNode leftAssociativeNode) {
      this.leftAssociativeNode = leftAssociativeNode;
      index = 1;
    }

    public boolean hasNext() {
      return index + 1 < leftAssociativeNode.getNumberOfChildren();
    }

    public OperatorAndOperand next() {
      OperatorAndOperand operatorAndOperand = new OperatorAndOperand(leftAssociativeNode.getChild(index).getType(),
          leftAssociativeNode.getChild(index + 1));

      index += 2;

      return operatorAndOperand;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public java.util.Iterator<OperatorAndOperand> iterator() {
      return this;
    }

  }

}
