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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.grammar.MutableLexerlessGrammar;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.NextExpression;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.NothingExpression;
import org.sonar.sslr.internal.vm.OneOrMoreExpression;
import org.sonar.sslr.internal.vm.OptionalExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.PatternExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.StringExpression;
import org.sonar.sslr.internal.vm.TokenExpression;
import org.sonar.sslr.internal.vm.TriviaExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.List;
import java.util.Map;

/**
 * A builder for creating grammars for lexerless parsing.
 *
 * @since 1.18
 */
public class LexerlessGrammarBuilder {

  private final Map<GrammarRuleKey, MutableParsingRule> definitions = Maps.newHashMap();
  private GrammarRuleKey rootRuleKey;

  public static LexerlessGrammarBuilder create() {
    return new LexerlessGrammarBuilder();
  }

  public static LexerlessGrammarBuilder createBasedOn(LexerlessGrammarBuilder... base) {
    return new LexerlessGrammarBuilder(base);
  }

  private LexerlessGrammarBuilder(LexerlessGrammarBuilder... base) {
    for (LexerlessGrammarBuilder b : base) {
      definitions.putAll(b.definitions);
    }
  }

  /**
   * Allows to describe rule.
   * Result of this method should be used only for execution of methods in it, i.e. you should not save reference on it.
   * No guarantee that this method always returns the same instance for the same key of rule.
   */
  public GrammarRuleBuilder rule(GrammarRuleKey ruleKey) {
    MutableParsingRule rule = definitions.get(ruleKey);
    if (rule == null) {
      rule = new MutableParsingRule(ruleKey);
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
   */
  public LexerlessGrammar build() {
    for (MutableParsingRule rule : definitions.values()) {
      if (rule.getExpression() == null) {
        throw new GrammarException("The rule '" + rule.getRuleKey() + "' hasn't beed defined.");
      }
    }
    return new MutableLexerlessGrammar(definitions, rootRuleKey);
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
   * @param e1  first sub-expression
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
   * @param e1  first sub-expression
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

  /**
   * Creates expression of grammar based on regular expression.
   *
   * @param regexp  regular expression
   * @throws java.util.regex.PatternSyntaxException if the expression's syntax is invalid
   */
  public Object regexp(String regexp) {
    return new PatternExpression(regexp);
  }

  /**
   * Creates expression of grammar - "end of input".
   */
  public Object endOfInput() {
    return EndOfInputExpression.INSTANCE;
  }

  /**
   * Creates expression of grammar - "token".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  public Object token(TokenType tokenType, Object e) {
    return new TokenExpression(tokenType, convertToExpression(e));
  }

  /**
   * Creates expression of grammar - "comment trivia".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  public Object commentTrivia(Object e) {
    return new TriviaExpression(TriviaKind.COMMENT, convertToExpression(e));
  }

  /**
   * Creates expression of grammar - "skipped trivia".
   *
   * @param e  sub-expression
   * @throws IllegalArgumentException if given argument is not a parsing expression
   */
  public Object skippedTrivia(Object e) {
    return new TriviaExpression(TriviaKind.SKIPPED_TEXT, convertToExpression(e));
  }

  @VisibleForTesting
  ParsingExpression convertToExpression(Object e) {
    if (e instanceof ParsingExpression) {
      return (ParsingExpression) e;
    } else if (e instanceof String) {
      return new StringExpression((String) e);
    } else if (e instanceof Character) {
      return new StringExpression(((Character) e).toString());
    } else if (e instanceof GrammarRuleKey) {
      GrammarRuleKey ruleKey = (GrammarRuleKey) e;
      rule(ruleKey);
      return definitions.get(ruleKey);
    } else {
      throw new IllegalArgumentException("Incorrect type of parsing expression: " + e.getClass().toString());
    }
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

    private final LexerlessGrammarBuilder b;
    private final MutableParsingRule delegate;

    public RuleBuilder(LexerlessGrammarBuilder b, MutableParsingRule delegate) {
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
