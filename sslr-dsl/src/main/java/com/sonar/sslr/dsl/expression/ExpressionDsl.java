/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.expression;

import static com.sonar.sslr.dsl.DefaultDslTokenType.DOUBLE;
import static com.sonar.sslr.dsl.DefaultDslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DefaultDslTokenType.WORD;
import static com.sonar.sslr.impl.matcher.Matchers.opt;
import static com.sonar.sslr.impl.matcher.Matchers.or;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.Dsl;

public class ExpressionDsl extends Dsl {

  public Rule expression;
  public Rule primaryExpression;
  public Rule multiplyExpression;
  public Rule divideExpression;
  public Rule substractExpression;
  public Rule addExpression;
  public Rule variable;
  public Rule parenthesisExpression;

  public ExpressionDsl() {
    variable.is(WORD).plug(VariableExpression.class);
    primaryExpression.isOr(INTEGER, DOUBLE).plug(PrimaryExpression.class);
    parenthesisExpression.is("(", expression, ")").plug(Expression.class);
    multiplyExpression.is(or(primaryExpression, parenthesisExpression, variable), opt("*", multiplyExpression)).skipIfOneChild()
        .plug(MultiplyExpression.class);
    divideExpression.is(multiplyExpression, opt("/", divideExpression)).skipIfOneChild().plug(DivideExpression.class);
    substractExpression.is(divideExpression, opt("-", substractExpression)).skipIfOneChild().plug(SubstractExpression.class);
    addExpression.is(substractExpression, opt("+", addExpression)).skipIfOneChild().plug(AddExpression.class);
    expression.is(addExpression).plug(Expression.class);
  }

  public Rule getRootRule() {
    return expression;
  }
}
