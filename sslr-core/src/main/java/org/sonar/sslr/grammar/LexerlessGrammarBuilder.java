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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.grammar.MutableGrammar;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.PatternExpression;
import org.sonar.sslr.internal.vm.StringExpression;
import org.sonar.sslr.internal.vm.TokenExpression;
import org.sonar.sslr.internal.vm.TriviaExpression;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.Map;

/**
 * A builder for creating grammars for lexerless parsing.
 *
 * @since 1.18
 * @see LexerfulGrammarBuilder
 */
public class LexerlessGrammarBuilder extends GrammarBuilder {

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

  public GrammarRuleBuilder rule(GrammarRuleKey ruleKey) {
    MutableParsingRule rule = definitions.get(ruleKey);
    if (rule == null) {
      rule = new MutableParsingRule(ruleKey);
      definitions.put(ruleKey, rule);
    }
    return new RuleBuilder(this, rule);
  }

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
    return new MutableGrammar(definitions, rootRuleKey);
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

  protected ParsingExpression convertToExpression(Object e) {
    Preconditions.checkNotNull(e, "Parsing expression can't be null");
    final ParsingExpression result;
    if (e instanceof ParsingExpression) {
      result = (ParsingExpression) e;
    } else if (e instanceof String) {
      result = new StringExpression((String) e);
    } else if (e instanceof Character) {
      result = new StringExpression(((Character) e).toString());
    } else if (e instanceof GrammarRuleKey) {
      GrammarRuleKey ruleKey = (GrammarRuleKey) e;
      rule(ruleKey);
      result = definitions.get(ruleKey);
    } else {
      throw new IllegalArgumentException("Incorrect type of parsing expression: " + e.getClass().toString());
    }
    return result;
  }

}
