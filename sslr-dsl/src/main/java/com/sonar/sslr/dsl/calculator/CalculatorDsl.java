/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.calculator;

import static com.sonar.sslr.dsl.DslTokenType.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

public class CalculatorDsl extends Grammar {

  public Rule expression;
  public Rule primaryExpression;
  public Rule multiplyExpression;
  public Rule divideExpression;
  public Rule substractExpression;
  public Rule addExpression;
  public Rule variable;
  public Rule parenthesisExpression;
  public Rule intValue;
  public Rule doubleValue;
  public Rule identifier;

  public CalculatorDsl() {
    variable.is(identifier).plug(VariableExpression.class);
    primaryExpression.isOr(intValue, doubleValue).plug(PrimaryExpression.class);
    parenthesisExpression.is("(", expression, ")").plug(Calculator.class);
    multiplyExpression.is(or(primaryExpression, parenthesisExpression, variable), opt("*", multiplyExpression)).skipIfOneChild();
    multiplyExpression.plug(MultiplyExpression.class);
    divideExpression.is(multiplyExpression, opt("/", divideExpression)).skipIfOneChild();
    divideExpression.plug(DivideExpression.class);
    substractExpression.is(divideExpression, opt("-", substractExpression)).skipIfOneChild();
    substractExpression.plug(SubstractExpression.class);
    addExpression.is(substractExpression, opt("+", addExpression)).skipIfOneChild();
    addExpression.plug(AddExpression.class);
    expression.is(addExpression).plug(Calculator.class);

    intValue.is(INTEGER).plug(Integer.class);
    doubleValue.is(DOUBLE).plug(Double.class);
    identifier.is(WORD).plug(String.class);

  }

  @Override
  public Rule getRootRule() {
    return expression;
  }
}
