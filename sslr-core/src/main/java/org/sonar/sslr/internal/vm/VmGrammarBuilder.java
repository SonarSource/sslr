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
package org.sonar.sslr.internal.vm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleBuilder;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;

import java.util.List;
import java.util.Map;

/**
 * A builder for creating grammars for lexerless parsing.
 *
 * TODO replacement for {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder}
 *
 * @since 1.18
 */
public class VmGrammarBuilder {

  public static VmGrammarBuilder create() {
    return new VmGrammarBuilder();
  }

  private final Map<GrammarRuleKey, ParsingRule> rules = Maps.newHashMap();
  private final Map<GrammarRuleKey, RuleRefExpression> refs = Maps.newHashMap();
  private GrammarRuleKey rootRuleKey;

  /**
   * Allows to describe rule.
   * Result of this method should be used only for execution of methods in it, i.e. you should not save reference on it.
   * No guarantee that this method always returns the same instance for the same key of rule.
   */
  public GrammarRuleBuilder rule(GrammarRuleKey ruleKey) {
    ParsingRule rule = rules.get(ruleKey);
    if (rule == null) {
      rule = new ParsingRule(this, ruleKey);
      rules.put(ruleKey, rule);
    }
    return rule;
  }

  /**
   * Allows to specify that given rule should be root for grammar.
   */
  public void setRootRule(GrammarRuleKey ruleKey) {
    rule(ruleKey);
    this.rootRuleKey = ruleKey;
  }

  /**
   * Constructs grammar.
   *
   * @throws GrammarException if some of rules were used, but not defined
   * @return grammar
   */
  public CompiledGrammar build() {
    Map<GrammarRuleKey, GrammarElementMatcher> matchers = Maps.newHashMap();
    // Compile all rules
    Map<GrammarRuleKey, Integer> offsets = Maps.newHashMap();
    Instruction[][] instr = new Instruction[rules.size()][];
    int offset = 0;
    int i = 0;
    for (Map.Entry<GrammarRuleKey, ParsingRule> e : rules.entrySet()) {
      offsets.put(e.getKey(), offset);
      ParsingExpression expression = e.getValue().getExpression();
      if (expression == null) {
        throw new GrammarException("The rule '" + e.getKey() + "' hasn't beed defined.");
      }
      matchers.put(e.getKey(), e.getValue().convert());
      instr[i] = expression.compile();
      offset += instr[i].length + 1;
      i++;
    }
    Instruction[] result = new Instruction[offset];
    offset = 0;
    for (i = 0; i < rules.size(); i++) {
      System.arraycopy(instr[i], 0, result, offset, instr[i].length);
      offset += instr[i].length + 1;
      result[offset - 1] = Instruction.ret();
    }

    // Link
    for (i = 0; i < result.length; i++) {
      Instruction instruction = result[i];
      if (instruction instanceof RuleRefExpression) {
        RuleRefExpression expression = (RuleRefExpression) instruction;
        GrammarRuleKey ruleKey = expression.getRuleKey();
        if (!offsets.containsKey(ruleKey)) {
          throw new GrammarException("The rule " + ruleKey + " has been used somewhere in grammar, but not defined.");
        }
        offset = offsets.get(ruleKey);
        result[i] = Instruction.call(offset - i, matchers.get(ruleKey));
      }
    }

    return new CompiledGrammar(result, offsets, matchers, rootRuleKey);
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
    // TODO there is no need to use TokenMatcher with new Grammar API (SSLR-284)
    return convertToExpression(e);
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

  ParsingExpression convertToExpression(Object e) {
    if (e instanceof ParsingExpression) {
      return (ParsingExpression) e;
    } else if (e instanceof String) {
      return new StringExpression((String) e);
    } else if (e instanceof Character) {
      return new StringExpression(((Character) e).toString());
    } else if (e instanceof GrammarRuleKey) {
      GrammarRuleKey ruleKey = (GrammarRuleKey) e;
      RuleRefExpression ref = refs.get(ruleKey);
      if (ref == null) {
        ref = new RuleRefExpression(ruleKey);
        refs.put(ruleKey, ref);
      }
      return ref;
    } else {
      throw new IllegalArgumentException("Incorrect type of parsing expression: " + e.getClass().toString());
    }
  }

  ParsingExpression[] convertToExpressions(List<Object> expressions) {
    ParsingExpression[] result = new ParsingExpression[expressions.size()];
    for (int i = 0; i < expressions.size(); i++) {
      result[i] = convertToExpression(expressions.get(i));
    }
    return result;
  }

}
