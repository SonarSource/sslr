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
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.firstOf;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.BREAK;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.CONTINUE;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.ELSE;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.IF;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.INT;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.RETURN;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.STRUCT;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.VOID;
import static com.sonar.sslr.test.miniC.MiniCLexer.Keywords.WHILE;
import static com.sonar.sslr.test.miniC.MiniCLexer.Literals.INTEGER;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.ADD;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.BRACE_L;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.BRACE_R;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.COMMA;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.DEC;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.DIV;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.EQ;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.EQEQ;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.GT;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.GTE;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.INC;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.LT;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.LTE;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.MUL;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.NE;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.PAREN_L;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.PAREN_R;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.SEMICOLON;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.SUB;

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

    binType.is(firstOf(
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

    definition.is(firstOf(
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

    statement.is(firstOf(
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

    relationalOperator.is(firstOf(
        EQEQ,
        NE,
        LT,
        LTE,
        GT,
        GTE
        ));

    additiveExpression.is(multiplicativeExpression, opt(additiveOperator, additiveExpression)).skipIfOneChild();

    additiveOperator.is(firstOf(
        ADD,
        SUB
        ));

    multiplicativeExpression.is(unaryExpression, opt(multiplicativeOperator, multiplicativeExpression)).skipIfOneChild();

    multiplicativeOperator.is(firstOf(
        MUL,
        DIV
        ));

    unaryExpression.is(firstOf(
        and(unaryOperator, primaryExpression),
        postfixExpression
        )).skipIfOneChild();

    unaryOperator.is(firstOf(
        INC,
        DEC
        ));

    postfixExpression.is(firstOf(
        and(primaryExpression, postfixOperator),
        and(binFunctionReference, PAREN_L, opt(argumentExpressionList), PAREN_R),
        primaryExpression
        )).skipIfOneChild();

    postfixOperator.is(firstOf(
        INC,
        DEC
        ));

    primaryExpression.is(firstOf(
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
