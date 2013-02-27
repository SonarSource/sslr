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
package org.sonar.sslr.grammar;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.sonar.sslr.internal.grammar.LexerfulGrammarAdapter;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.NextExpression;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.NothingExpression;
import org.sonar.sslr.internal.vm.OneOrMoreExpression;
import org.sonar.sslr.internal.vm.OptionalExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;
import org.sonar.sslr.internal.vm.lexerful.AdjacentExpression;
import org.sonar.sslr.internal.vm.lexerful.AnyTokenExpression;
import org.sonar.sslr.internal.vm.lexerful.TillNewLineExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypeClassExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypeExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypesExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenValueExpression;
import org.sonar.sslr.internal.vm.lexerful.TokensBridgeExpression;

import java.util.List;
import java.util.Map;

/**
 * A builder for creating grammars for lexerful parsing.
 *
 * @since 1.18
 * @see LexerlessGrammarBuilder
 */
public class LexerfulGrammarBuilder {

  private final Map<GrammarRuleKey, RuleDefinition> definitions = Maps.newHashMap();
  private GrammarRuleKey rootRuleKey;

  public static LexerfulGrammarBuilder create() {
    return new LexerfulGrammarBuilder();
  }

  public static LexerfulGrammarBuilder createBasedOn(LexerfulGrammarBuilder... base) {
    return new LexerfulGrammarBuilder(base);
  }

  private LexerfulGrammarBuilder(LexerfulGrammarBuilder... base) {
    for (LexerfulGrammarBuilder b : base) {
      definitions.putAll(b.definitions);
    }
  }

  /**
   * Allows to describe rule.
   * Result of this method should be used only for execution of methods in it, i.e. you should not save reference on it.
   * No guarantee that this method always returns the same instance for the same key of rule.
   */
  public GrammarRuleBuilder rule(GrammarRuleKey ruleKey) {
    RuleDefinition rule = definitions.get(ruleKey);
    if (rule == null) {
      rule = new RuleDefinition(ruleKey);
      definitions.put(ruleKey, rule);
    }
    return new RuleBuilder(this, rule);
  }

  /**
   * Allows to specify that given rule should be root for grammar.
   */
  public void setRootRule(GrammarRuleKey ruleKey) {
    rule(ruleKey);
    rootRuleKey = ruleKey;
  }

  /**
   * Constructs grammar.
   *
   * @throws GrammarException if some of rules were used, but not defined
   * @return grammar
   * @see #buildWithMemoizationOfMatchesForAllRules()
   */
  public Grammar build() {
    for (RuleDefinition rule : definitions.values()) {
      if (rule.getExpression() == null) {
        throw new GrammarException("The rule '" + rule.getRuleKey() + "' hasn't beed defined.");
      }
    }
    return new LexerfulGrammarAdapter(definitions, rootRuleKey);
  }

  /**
   * Constructs grammar with memoization of matches for all rules.
   *
   * @throws GrammarException if some of rules were used, but not defined
   * @return grammar
   * @see #build()
   */
  public Grammar buildWithMemoizationOfMatchesForAllRules() {
    // TODO
    return build();
  }

  /**
   * Creates expression of grammar - "sequence".
   *
   * @param e1  first sub-expression
   * @param e2  second sub-expression
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object sequence(Object e1, Object e2) {
    return new SequenceExpression(convertToExpression(e1), convertToExpression(e2));
  }

  /**
   * Creates expression of grammar - "sequence".
   *
   * @param e1  first sub-expression
   * @param e2  second sub-expression
   * @param rest  rest of sub-expressions
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object sequence(Object e1, Object e2, Object... rest) {
    return new SequenceExpression(convertToExpressions(Lists.asList(e1, e2, rest)));
  }

  /**
   * Creates expression of grammar - "first of".
   *
   * @param e1  first sub-expression
   * @param e2  second sub-expression
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object firstOf(Object e1, Object e2) {
    return new FirstOfExpression(convertToExpression(e1), convertToExpression(e2));
  }

  /**
   * Creates expression of grammar - "first of".
   *
   * @param e1  first sub-expression
   * @param e2  second sub-expression
   * @param rest  rest of sub-expressions
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object firstOf(Object e1, Object e2, Object... rest) {
    return new FirstOfExpression(convertToExpressions(Lists.asList(e1, e2, rest)));
  }

  /**
   * Creates expression of grammar - "optional".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  public Object optional(Object e) {
    return new OptionalExpression(convertToExpression(e));
  }

  /**
   * Creates expression of grammar - "optional".
   *
   * @param e1  first sub-expression
   * @param rest  rest of sub-expressions
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object optional(Object e1, Object... rest) {
    return new OptionalExpression(new SequenceExpression(convertToExpressions(Lists.asList(e1, rest))));
  }

  /**
   * Creates expression of grammar - "one or more".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  public Object oneOrMore(Object e) {
    return new OneOrMoreExpression(convertToExpression(e));
  }

  /**
   * Creates expression of grammar - "one or more".
   *
   * @param e1  first sub-expression
   * @param rest  rest of sub-expressions
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object oneOrMore(Object e1, Object... rest) {
    return new OneOrMoreExpression(new SequenceExpression(convertToExpressions(Lists.asList(e1, rest))));
  }

  /**
   * Creates expression of grammar - "zero or more".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  public Object zeroOrMore(Object e) {
    return new ZeroOrMoreExpression(convertToExpression(e));
  }

  /**
   * Creates expression of grammar - "zero or more".
   *
   * @param e1  sub-expression
   * @param rest  rest of sub-expressions
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object zeroOrMore(Object e1, Object... rest) {
    return new ZeroOrMoreExpression(new SequenceExpression(convertToExpressions(Lists.asList(e1, rest))));
  }

  /**
   * Creates expression of grammar - "next".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  public Object next(Object e) {
    return new NextExpression(convertToExpression(e));
  }

  /**
   * Creates expression of grammar - "next".
   *
   * @param e1  first sub-expression
   * @param rest  rest of sub-expressions
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object next(Object e1, Object... rest) {
    return new NextExpression(new SequenceExpression(convertToExpressions(Lists.asList(e1, rest))));
  }

  /**
   * Creates expression of grammar - "next not".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  public Object nextNot(Object e) {
    return new NextNotExpression(convertToExpression(e));
  }

  /**
   * Creates expression of grammar - "next not".
   *
   * @param e1  sub-expression
   * @param rest  rest of sub-expressions
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object nextNot(Object e1, Object... rest) {
    return new NextNotExpression(new SequenceExpression(convertToExpressions(Lists.asList(e1, rest))));
  }

  /**
   * Creates expression of grammar - "nothing".
   */
  public Object nothing() {
    return NothingExpression.INSTANCE;
  }

  public Object adjacent(Object e) {
    return new SequenceExpression(AdjacentExpression.INSTANCE, convertToExpression(e));
  }

  public Object anyTokenButNot(Object e) {
    return new SequenceExpression(new NextNotExpression(convertToExpression(e)), AnyTokenExpression.INSTANCE);
  }

  public Object isOneOfThem(TokenType t1, TokenType... others) {
    TokenType[] types = new TokenType[1 + others.length];
    types[0] = t1;
    System.arraycopy(others, 0, types, 1, others.length);
    return new TokenTypesExpression(types);
  }

  public Object bridge(TokenType from, TokenType to) {
    return new TokensBridgeExpression(from, to);
  }

  /**
   * @deprecated in 1.19, use {@link #anyToken()} instead.
   */
  @Deprecated
  public Object everything() {
    return AnyTokenExpression.INSTANCE;
  }

  /**
   * Creates expression of grammar - "any token".
   */
  public Object anyToken() {
    return AnyTokenExpression.INSTANCE;
  }

  /**
   * Creates expression of grammar - "till new line".
   */
  public Object tillNewLine() {
    return TillNewLineExpression.INSTANCE;
  }

  /**
   * Creates expression of grammar - "till".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  public Object till(Object e) {
    // TODO repeated expression
    ParsingExpression expression = convertToExpression(e);
    return new SequenceExpression(
        new ZeroOrMoreExpression(
            new SequenceExpression(
                new NextNotExpression(expression),
                AnyTokenExpression.INSTANCE)),
        expression);
  }

  /**
   * Creates expression of grammar - "exclusive till".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object exclusiveTill(Object e) {
    return new ZeroOrMoreExpression(
        new SequenceExpression(
            new NextNotExpression(convertToExpression(e)),
            AnyTokenExpression.INSTANCE));
  }

  /**
   * Creates expression of grammar - "exclusive till".
   *
   * @param e1  first sub-expression
   * @param rest  rest of sub-expressions
   * @throws IllegalArgumentException if any of given arguments is not a parsing expression
   */
  public Object exclusiveTill(Object e1, Object... rest) {
    return exclusiveTill(new FirstOfExpression(convertToExpressions(Lists.asList(e1, rest))));
  }

  @VisibleForTesting
  ParsingExpression convertToExpression(Object e) {
    Preconditions.checkNotNull(e, "Parsing expression can't be null");
    final ParsingExpression result;
    if (e instanceof ParsingExpression) {
      result = (ParsingExpression) e;
    } else if (e instanceof String) {
      result = new TokenValueExpression((String) e);
    } else if (e instanceof TokenType) {
      result = new TokenTypeExpression((TokenType) e);
    } else if (e instanceof Class) {
      result = new TokenTypeClassExpression((Class) e);
    } else if (e instanceof GrammarRuleKey) {
      GrammarRuleKey ruleKey = (GrammarRuleKey) e;
      rule(ruleKey);
      result = definitions.get(ruleKey);
    } else {
      throw new IllegalArgumentException("Incorrect type of parsing expression: " + e.getClass().toString());
    }
    return result;
  }

  private ParsingExpression[] convertToExpressions(List<Object> expressions) {
    ParsingExpression[] result = new ParsingExpression[expressions.size()];
    for (int i = 0; i < expressions.size(); i++) {
      result[i] = convertToExpression(expressions.get(i));
    }
    return result;
  }

  @VisibleForTesting
  static class RuleBuilder implements GrammarRuleBuilder {

    private final LexerfulGrammarBuilder b;
    private final RuleDefinition delegate;

    public RuleBuilder(LexerfulGrammarBuilder b, RuleDefinition delegate) {
      this.b = b;
      this.delegate = delegate;
    }

    public GrammarRuleBuilder is(Object e) {
      if (delegate.getExpression() != null) {
        throw new GrammarException("The rule '" + delegate.getRuleKey() + "' has already been defined somewhere in the grammar.");
      }
      delegate.setExpression(b.convertToExpression(e));
      return this;
    }

    public GrammarRuleBuilder is(Object e, Object... rest) {
      return is(new SequenceExpression(b.convertToExpressions(Lists.asList(e, rest))));
    }

    public GrammarRuleBuilder override(Object e) {
      delegate.setExpression(b.convertToExpression(e));
      return this;
    }

    public GrammarRuleBuilder override(Object e, Object... rest) {
      return override(new SequenceExpression(b.convertToExpressions(Lists.asList(e, rest))));
    }

    public void skip() {
      delegate.skip();
    }

    public void skipIfOneChild() {
      delegate.skipIfOneChild();
    }

    public void recoveryRule() {
      throw new UnsupportedOperationException();
    }

  }

}
