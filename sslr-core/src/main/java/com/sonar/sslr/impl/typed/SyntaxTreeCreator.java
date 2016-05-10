/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.impl.typed;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.api.Trivia.TriviaKind;
import com.sonar.sslr.api.typed.Input;
import com.sonar.sslr.api.typed.NodeBuilder;
import com.sonar.sslr.api.typed.Optional;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.matchers.ParseNode;
import org.sonar.sslr.internal.vm.TokenExpression;
import org.sonar.sslr.internal.vm.TriviaExpression;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyntaxTreeCreator<T> {

  private final Object treeFactory;
  private final GrammarBuilderInterceptor mapping;
  private final NodeBuilder nodeBuilder;

  private final Token.Builder tokenBuilder = Token.builder();
  private final List<Trivia> trivias = new ArrayList<>();

  private Input input;

  public SyntaxTreeCreator(Object treeFactory, GrammarBuilderInterceptor mapping, NodeBuilder nodeBuilder) {
    this.treeFactory = treeFactory;
    this.mapping = mapping;
    this.nodeBuilder = nodeBuilder;
  }

  public T create(ParseNode node, Input input) {
    this.input = input;
    this.trivias.clear();
    T result = (T) visit(node);
    if (result instanceof AstNode) {
      ((AstNode) result).hasToBeSkippedFromAst();
    }
    return result;
  }

  private Object visit(ParseNode node) {
    if (node.getMatcher() instanceof MutableParsingRule) {
      return visitNonTerminal(node);
    } else {
      return visitTerminal(node);
    }
  }

  private Object visitNonTerminal(ParseNode node) {
    MutableParsingRule rule = (MutableParsingRule) node.getMatcher();
    GrammarRuleKey ruleKey = rule.getRuleKey();
    Method method = mapping.actionForRuleKey(ruleKey);

    Object result;

    if (mapping.hasMethodForRuleKey(ruleKey)) {

      // TODO Drop useless intermediate nodes
      Preconditions.checkState(node.getChildren().size() == 1);
      result = visit(node.getChildren().get(0));

    } else if (mapping.isOptionalRule(ruleKey)) {

      Preconditions.checkState(node.getChildren().size() <= 1);
      if (node.getChildren().isEmpty()) {
        result = Optional.absent();
      } else {
        Object child = visit(node.getChildren().get(0));
        if (child instanceof AstNode) {
          ((AstNode) child).hasToBeSkippedFromAst();
        }
        result = Optional.of(child);
      }

    } else if (mapping.isOneOrMoreRule(ruleKey)) {

      result = convertChildren(node);

    } else if (mapping.isZeroOrMoreRule(ruleKey)) {

      List<Object> convertedChildren = convertChildren(node);
      result = convertedChildren.isEmpty() ? Optional.absent() : Optional.of(convertedChildren);

    } else if (method == null) {

      result = nodeBuilder.createNonTerminal(rule.getRuleKey(), rule, convertChildren(node), node.getStartIndex(), node.getEndIndex());

    } else {

      List<Object> convertedChildren = convertChildren(node);
      result = ReflectionUtils.invokeMethod(method, treeFactory, convertedChildren.toArray(new Object[convertedChildren.size()]));

    }
    return result;
  }

  private List<Object> convertChildren(ParseNode node) {
    List<Object> convertedChildren = new ArrayList<>();
    for (ParseNode child : node.getChildren()) {
      Object result = visit(child);

      if (result != null) {
        // FIXME to remove aafter full migration: Allow to skip optional nodes that are supposed to bw skipped from the AST
        if (result instanceof Optional && ((Optional) result).isPresent() && hasToBeSkippedFromAst(((Optional) result).get())) {
          for (AstNode resultChild : ((AstNode) ((Optional) result).get()).getChildren()) {
            convertedChildren.add(resultChild);
          }

        } else if (hasToBeSkippedFromAst(result)) {
          for (AstNode resultChild : ((AstNode) result).getChildren()) {
            convertedChildren.add(resultChild);
          }
        } else {
          convertedChildren.add(result);
        }
      }
    }
    return convertedChildren;
  }

  private static boolean hasToBeSkippedFromAst(Object object) {
    return object instanceof AstNode && ((AstNode) object).hasToBeSkippedFromAst();
  }

  private Object visitTerminal(ParseNode node) {
    TokenType type = null;
    if (node.getMatcher() instanceof TriviaExpression) {
      TriviaExpression ruleMatcher = (TriviaExpression) node.getMatcher();
      if (ruleMatcher.getTriviaKind() == TriviaKind.SKIPPED_TEXT) {
        return null;
      } else if (ruleMatcher.getTriviaKind() == TriviaKind.COMMENT) {
        addComment(node);
        return null;
      } else {
        throw new IllegalStateException("Unexpected trivia kind: " + ruleMatcher.getTriviaKind());
      }
    } else if (node.getMatcher() instanceof TokenExpression) {
      TokenExpression ruleMatcher = (TokenExpression) node.getMatcher();
      type = ruleMatcher.getTokenType();
      if (GenericTokenType.COMMENT.equals(ruleMatcher.getTokenType())) {
        addComment(node);
        return null;
      }
    }
    Object result = nodeBuilder.createTerminal(input, node.getStartIndex(), node.getEndIndex(), trivias, type);
    trivias.clear();
    return result;
  }

  private void addComment(ParseNode node) {
    tokenBuilder.setGeneratedCode(false);
    int[] lineAndColumn = input.lineAndColumnAt(node.getStartIndex());
    tokenBuilder.setLine(lineAndColumn[0]);
    tokenBuilder.setColumn(lineAndColumn[1] - 1);
    tokenBuilder.setURI(input.uri());
    String value = input.substring(node.getStartIndex(), node.getEndIndex());
    tokenBuilder.setValueAndOriginalValue(value);
    tokenBuilder.setTrivia(Collections.<Trivia>emptyList());
    tokenBuilder.setType(GenericTokenType.COMMENT);
    trivias.add(Trivia.createComment(tokenBuilder.build()));
  }

}
