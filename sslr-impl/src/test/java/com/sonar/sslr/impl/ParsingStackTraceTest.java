/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import static com.sonar.sslr.impl.matcher.Matchers.one2n;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.Matchers;
import com.sonar.sslr.impl.matcher.RuleImpl;
import com.sonar.sslr.impl.matcher.TokenValueMatcher;

public class ParsingStackTraceTest {

  private List<Token> tokens = new ArrayList<Token>();
  TokenValueMatcher language = new TokenValueMatcher("language");
  private ParsingState state;

  @Before
  public void init() {
    tokens.add(new Token(MockTokenType.WORD, "public"));
    tokens.add(new Token(MockTokenType.WORD, "java"));
    tokens.add(new Token(MockTokenType.WORD, "lang", 34, 46, new File("file1")));
    Token copyBookToken = new Token(MockTokenType.WORD, "class", 34, 46, new File("copy1"));
    copyBookToken.setCopyBook(true);
    copyBookToken.setCopyBookOriginalFileName("file1");
    copyBookToken.setCopyBookOriginalLine(10);
    tokens.add(copyBookToken);

    state = new ParsingState(tokens);
    RuleImpl parentRule = new RuleImpl("ParentRule");
    parentRule.isOr(Matchers.or(language, "implements"));
    RuleImpl grandParentRule = new RuleImpl("GrandParentRule");
    grandParentRule.is(one2n(parentRule));
    state.popToken(parentRule);
    state.popToken(parentRule);
  }

  @Test
  public void testGenerate() {
    state.peekToken(language);

    StringBuilder expected = new StringBuilder();
    expected.append("Expected : <language> but was : <lang [WORD]> ('file1': Line 34 / Column 46)\n");
    expected.append("  at ParentRule := ((language | implements))\n");
    expected.append("  at GrandParentRule := (ParentRule)+\n");

    assertEquals(expected.toString(), ParsingStackTrace.generate(state));
  }

  @Test
  public void testGenerateErrorOnCopyBook() {
    state.popToken(language);
    state.peekToken(language);

    StringBuilder expected = new StringBuilder();
    expected.append("Expected : <language> but was : <class [WORD]> (copy book 'copy1': Line 34 / Column 46 called from file 'file1': Line 10)\n");
    expected.append("  at ParentRule := ((language | implements))\n");
    expected.append("  at GrandParentRule := (ParentRule)+\n");

    assertEquals(expected.toString(), ParsingStackTrace.generate(state));
  }

  @Test
  public void testEndOfFileIsReached() {
    state.popToken(language);
    state.popToken(language);

    try {
      state.peekToken(language);
    } catch (RecognitionExceptionImpl e) {

    }

    StringBuilder expected = new StringBuilder();
    expected.append("Expected : <language> but was : <EOF> ('copy1')\n");
    expected.append("  at ParentRule := ((language | implements))\n");
    expected.append("  at GrandParentRule := (ParentRule)+\n");

    assertEquals(expected.toString(), ParsingStackTrace.generate(state));
  }

}
