/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.*;
import static com.sonar.sslr.test.miniC.MiniCLexer.Literals.INTEGER;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.*;

public class MiniCGrammar extends Grammar {

  public Rule binType;
  public Rule binFunctionDefinition;
  public Rule binParameter;
  public Rule binVariableDefinition;
  public Rule binFunctionReference;
  public Rule binVariableReference;

  public Rule compilationUnit;
  public Rule definition;
  public Rule structDefinition;
  public Rule structMember;
  public Rule functionDefinition;
  public Rule variableDefinition;
  public Rule parametersList;
  public Rule parameterDeclaration;
  public Rule compoundStatement;
  public Rule variableInitializer;
  public Rule argumentExpressionList;

  public Rule statement;
  public Rule expressionStatement;
  public Rule returnStatement;
  public Rule continueStatement;
  public Rule breakStatement;
  public Rule ifStatement;
  public Rule whileStatement;
  public Rule conditionClause;
  public Rule elseClause;
  public Rule noComplexityStatement;

  public Rule expression;
  public Rule assignmentExpression;
  public Rule relationalExpression;
  public Rule relationalOperator;
  public Rule additiveExpression;
  public Rule additiveOperator;
  public Rule multiplicativeExpression;
  public Rule multiplicativeOperator;
  public Rule unaryExpression;
  public Rule unaryOperator;
  public Rule postfixExpression;
  public Rule postfixOperator;
  public Rule primaryExpression;

  public MiniCGrammar() {
    // Bins

    binType.is(or(
        INT,
        VOID
        ));

    binParameter.is(IDENTIFIER);

    binFunctionDefinition.is(IDENTIFIER);

    binVariableDefinition.is(IDENTIFIER);

    binFunctionReference.is(IDENTIFIER);

    binVariableReference.is(IDENTIFIER);

    // Miscellaneous

    compilationUnit.is(o2n(definition), EOF);

    definition.is(or(
        structDefinition,
        functionDefinition,
        variableDefinition
        ));

    structDefinition.is(STRUCT, IDENTIFIER, BRACE_L, one2n(structMember, SEMICOLON), BRACE_R);

    structMember.is(binType, IDENTIFIER);

    functionDefinition.is(binType, binFunctionDefinition, PAREN_L, opt(parametersList), PAREN_R, compoundStatement);

    variableDefinition.is(binType, binVariableDefinition, opt(variableInitializer), SEMICOLON);

    parametersList.is(parameterDeclaration, o2n(COMMA, parameterDeclaration));

    parameterDeclaration.is(binType, binParameter);

    compoundStatement.is(BRACE_L, o2n(variableDefinition), o2n(statement), BRACE_R);

    variableInitializer.is(EQ, expression);

    argumentExpressionList.is(expression, o2n(COMMA, expression));

    // Statements

    statement.is(or(
        expressionStatement,
        compoundStatement,
        returnStatement,
        continueStatement,
        breakStatement,
        ifStatement,
        whileStatement,
        noComplexityStatement
        ));

    expressionStatement.is(expression, SEMICOLON);

    returnStatement.is(RETURN, expression, SEMICOLON);

    continueStatement.is(CONTINUE, SEMICOLON);

    breakStatement.is(BREAK, SEMICOLON);

    ifStatement.is(IF, conditionClause, statement, opt(elseClause));

    whileStatement.is(WHILE, conditionClause, statement);

    conditionClause.is(PAREN_L, expression, PAREN_R);

    elseClause.is(ELSE, statement);

    noComplexityStatement.is("nocomplexity", statement);

    // Expressions

    expression.is(assignmentExpression);

    assignmentExpression.is(relationalExpression, opt(EQ, relationalExpression)).skipIfOneChild();

    relationalExpression.is(additiveExpression, opt(relationalOperator, relationalExpression)).skipIfOneChild();

    relationalOperator.is(or(
        EQEQ,
        NE,
        LT,
        LTE,
        GT,
        GTE
        ));

    additiveExpression.is(multiplicativeExpression, opt(additiveOperator, additiveExpression)).skipIfOneChild();

    additiveOperator.is(or(
        ADD,
        SUB
        ));

    multiplicativeExpression.is(unaryExpression, opt(multiplicativeOperator, multiplicativeExpression)).skipIfOneChild();

    multiplicativeOperator.is(or(
        MUL,
        DIV
        ));

    unaryExpression.is(or(
        and(unaryOperator, primaryExpression),
        postfixExpression
        )).skipIfOneChild();

    unaryOperator.is(or(
        INC,
        DEC
        ));

    postfixExpression.is(or(
        and(primaryExpression, postfixOperator),
        and(binFunctionReference, PAREN_L, opt(argumentExpressionList), PAREN_R),
        primaryExpression
        )).skipIfOneChild();

    postfixOperator.is(or(
        INC,
        DEC
        ));

    primaryExpression.is(or(
        INTEGER,
        binVariableReference,
        and(PAREN_L, expression, PAREN_R)
        ));
  }

  @Override
  public Rule getRootRule() {
    return compilationUnit;
  }

}
