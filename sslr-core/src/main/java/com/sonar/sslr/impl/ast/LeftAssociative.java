/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
 */
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

    @Override
    public boolean hasNext() {
      return index + 1 < leftAssociativeNode.getNumberOfChildren();
    }

    @Override
    public OperatorAndOperand next() {
      OperatorAndOperand operatorAndOperand = new OperatorAndOperand(leftAssociativeNode.getChild(index).getType(),
          leftAssociativeNode.getChild(index + 1));

      index += 2;

      return operatorAndOperand;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Iterator<OperatorAndOperand> iterator() {
      return this;
    }

  }

}
