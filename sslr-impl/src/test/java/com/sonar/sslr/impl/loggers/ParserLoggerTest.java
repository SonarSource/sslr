/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.loggers;

import static com.sonar.sslr.impl.matcher.Matchers.and;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.LeftRecursiveRuleImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

@Ignore
public class ParserLoggerTest {

  private static Logger logger;

  @BeforeClass
  public static void init() {
    logger = mock(Logger.class);
    SslrLogger.createSslrLogger(logger);
  }

  @Test
  public void testTryToMatchAndHasMatched() throws Exception {
    RuleImpl rule = new RuleImpl("rule");
    rule.is("A");
    List<Token> tokens = Lists.newArrayList();
    Token token = new Token(GenericTokenType.LITERAL, "A");
    tokens.add(token);
    ParsingState parsingState = new ParsingState(tokens);

    rule.match(parsingState);

    verify(logger).tryToMatch(rule, parsingState);
    verify(logger).hasMatched(eq(rule), eq(parsingState), any(AstNode.class));
  }

  @Test
  public void testMemoizedAstUsed() throws Exception {
    RuleImpl recursiveRule = new LeftRecursiveRuleImpl("recursiveRule");
    recursiveRule.isOr(and(recursiveRule, "+", "1"), "1");
    List<Token> tokens = Lists.newArrayList();
    tokens.add(new Token(GenericTokenType.LITERAL, "1"));
    tokens.add(new Token(GenericTokenType.LITERAL, "+"));
    tokens.add(new Token(GenericTokenType.LITERAL, "1"));
    ParsingState parsingState = new ParsingState(tokens);

    recursiveRule.match(parsingState);

    verify(logger).memoizedAstUsed(any(Matcher.class), eq(parsingState), any(AstNode.class));
  }

}
