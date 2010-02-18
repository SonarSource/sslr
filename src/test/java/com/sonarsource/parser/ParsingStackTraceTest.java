/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonarsource.lexer.Token;
import com.sonarsource.parser.matcher.Matchers;
import com.sonarsource.parser.matcher.Rule;
import com.sonarsource.parser.matcher.TokenValueMatcher;

import static com.sonarsource.parser.matcher.Matchers.one2n;

import static org.junit.Assert.assertEquals;

public class ParsingStackTraceTest {

  private List<Token> tokens = new ArrayList<Token>();
  private ParsingState state;

  @Before
  public void init() {
    tokens.add(new Token(MockTokenType.WORD, "public"));
    tokens.add(new Token(MockTokenType.WORD, "java"));
    tokens.add(new Token(MockTokenType.WORD, "lang", 34, 46, "file1"));
    tokens.add(new Token(MockTokenType.WORD, "class", 34, 46, "file2"));
    state = new ParsingState(tokens);
  }

  @Test
  public void testGenerate() {
    TokenValueMatcher language = new TokenValueMatcher("language");
    Rule parentRule = new Rule("ParentRule");
    parentRule.or(Matchers.or(language, "implements"));
    Rule grandParentRule = new Rule("GrandParentRule");
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
    ParsingState state = new ParsingState(tokens);
    TokenValueMatcher language = new TokenValueMatcher("language");
    Rule parentRule = new Rule("ParentRule");
    parentRule.or(Matchers.or(language, "implements"));
    Rule grandParentRule = new Rule("GrandParentRule");
    grandParentRule.is(one2n(parentRule));
    state.popToken(parentRule);
    state.popToken(parentRule);
    state.popToken(parentRule);
    state.popToken(parentRule);
    try {
      state.peekToken(language);
    } catch (RecognitionException e) {

    }

    StringBuilder expected = new StringBuilder();
    expected.append("Expected : <language> but was : <EOF> ('file2')\n");
    expected.append("  at ParentRule := ((language | implements))\n");
    expected.append("  at GrandParentRule := (ParentRule)+\n");

    assertEquals(expected.toString(), ParsingStackTrace.generate(state));
  }

}
