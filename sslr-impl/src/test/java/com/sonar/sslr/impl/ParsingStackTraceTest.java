/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingStackTrace;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matchers;
import com.sonar.sslr.impl.matcher.RuleImpl;
import com.sonar.sslr.impl.matcher.TokenValueMatcher;

import static com.sonar.sslr.impl.matcher.Matchers.one2n;

import static org.junit.Assert.assertEquals;

public class ParsingStackTraceTest {

  private List<Token> tokens = new ArrayList<Token>();
  private ParsingState state;

  @Before
  public void init() {
    tokens.add(new Token(MockTokenType.WORD, "public"));
    tokens.add(new Token(MockTokenType.WORD, "java"));
    tokens.add(new Token(MockTokenType.WORD, "lang", 34, 46, new File("file1")));
    tokens.add(new Token(MockTokenType.WORD, "class", 34, 46, new File("file2")));

    LexerOutput lexerOutput = new LexerOutput();
    lexerOutput.addAllTokens(tokens);
    state = new ParsingState(lexerOutput);
  }

  @Test
  public void testGenerate() {
    TokenValueMatcher language = new TokenValueMatcher("language");
    RuleImpl parentRule = new RuleImpl("ParentRule");
    parentRule.isOr(Matchers.or(language, "implements"));
    RuleImpl grandParentRule = new RuleImpl("GrandParentRule");
    grandParentRule.is(one2n(parentRule));
    state.popToken(parentRule);
    state.popToken(parentRule);
    state.peekToken(language);

    StringBuilder expected = new StringBuilder();
    expected.append("Expected : <language> but was : <lang [WORD]> ('file1': Line 34 / Column 46)\n");
    expected.append("  at ParentRule := ((language | implements))\n");
    expected.append("  at GrandParentRule := (ParentRule)+\n");

    assertEquals(expected.toString(), ParsingStackTrace.generate(state));
  }

  @Test
  public void testEndOfFileIsReached() {
    LexerOutput lexerOutput = new LexerOutput();
    lexerOutput.addAllTokens(tokens);
    ParsingState state = new ParsingState(lexerOutput);
    TokenValueMatcher language = new TokenValueMatcher("language");
    RuleImpl parentRule = new RuleImpl("ParentRule");
    parentRule.isOr(Matchers.or(language, "implements"));
    RuleImpl grandParentRule = new RuleImpl("GrandParentRule");
    grandParentRule.is(one2n(parentRule));
    state.popToken(parentRule);
    state.popToken(parentRule);
    state.popToken(parentRule);
    state.popToken(parentRule);
    try {
      state.peekToken(language);
    } catch (RecognitionExceptionImpl e) {

    }

    StringBuilder expected = new StringBuilder();
    expected.append("Expected : <language> but was : <EOF> ('file2')\n");
    expected.append("  at ParentRule := ((language | implements))\n");
    expected.append("  at GrandParentRule := (ParentRule)+\n");

    assertEquals(expected.toString(), ParsingStackTrace.generate(state));
  }

}
