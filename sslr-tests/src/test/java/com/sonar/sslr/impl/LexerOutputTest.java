/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;

public class LexerOutputTest {

  @Test
  public void testRemoveLastTokens() {
    LexerOutput output = new LexerOutput();
    output.addToken(new Token(GenericTokenType.IDENTIFIER, "word1"));
    output.addToken(new Token(GenericTokenType.IDENTIFIER, "word2"));
    output.addToken(new Token(GenericTokenType.IDENTIFIER, "word3"));
    output.addToken(new Token(GenericTokenType.IDENTIFIER, "word4"));

    assertThat(output.size(), is(4));

    output.removeLastTokens(3);
    assertThat(output.size(), is(1));
    assertThat(output.getLastToken().getValue(), is("word1"));
  }

  @Test
  public void testAddPreprocessingToken() {
    LexerOutput output = new LexerOutput();
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);
    output.addPreprocessingToken(new Token(GenericTokenType.IDENTIFIER, "preprocessingWord"));

    assertThat(output.size(), is(1));
    assertThat(output.getLastToken().getValue(), is("Word"));

    assertThat(output.getPreprocessingTokens().size(), is(1));
    assertThat(output.getPreprocessingTokens().get(0).getValue(), is("preprocessingWord"));
  }

  @Test
  public void testNoTrivia() {
    LexerOutput output = new LexerOutput();
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);
    assertThat(output.size(), is(1));
    assertThat(output.get(0).getTrivia().size(), is(0));
  }

  @Test
  public void testCommentTrivia() {
    LexerOutput output = new LexerOutput();

    Token fakeCommentToken = new Token(GenericTokenType.IDENTIFIER, "Word", 10, 4);
    output.addCommentToken(fakeCommentToken);

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    assertThat(output.size(), is(1));
    assertThat(output.get(0).getTrivia().size(), is(1));
    assertThat(output.get(0).getTrivia().get(0), is(fakeCommentToken));
    assertThat(output.get(0).getTrivia().get(0).isCommentTrivia(), is(true));
    assertThat(output.get(0).getTrivia().get(0).isPreprocessorTrivia(), is(false));
  }

  @Test
  public void testPreprocessorTrivia() {
    LexerOutput output = new LexerOutput();

    Token fakePreprocessorToken = new Token(GenericTokenType.IDENTIFIER, "Word", 10, 4);
    output.addPreprocessingToken(fakePreprocessorToken);

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    assertThat(output.size(), is(1));
    assertThat(output.get(0).getTrivia().size(), is(1));
    assertThat(output.get(0).getTrivia().get(0), is(fakePreprocessorToken));
    assertThat(output.get(0).getTrivia().get(0).isCommentTrivia(), is(false));
    assertThat(output.get(0).getTrivia().get(0).isPreprocessorTrivia(), is(true));
  }

  @Test
  public void testTrivaNotAttachedIfTokenBefore() {
    LexerOutput output = new LexerOutput();
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    Token fakeCommentToken = new Token(GenericTokenType.IDENTIFIER, "Word", 10, 4);
    output.addCommentToken(fakeCommentToken);

    assertThat(output.size(), is(1));
    assertThat(output.get(0).getTrivia().size(), is(0));
  }

  @Test
  public void testTriviaAttachedToRightToken() {
    LexerOutput output = new LexerOutput();

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    Token fakeCommentToken = new Token(GenericTokenType.IDENTIFIER, "Word", 10, 4);
    output.addPreprocessingToken(fakeCommentToken);

    Token fakePreprocessorToken = new Token(GenericTokenType.IDENTIFIER, "Word", 10, 4);
    output.addPreprocessingToken(fakePreprocessorToken);

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    assertThat(output.size(), is(2));
    assertThat(output.get(0).getTrivia().size(), is(0));
    assertThat(output.get(1).getTrivia().size(), is(2));
    assertThat(output.get(1).getTrivia().get(0), is(fakeCommentToken));
    assertThat(output.get(1).getTrivia().get(1), is(fakePreprocessorToken));
  }

  @Test
  public void testTrivaWithRemoveLastTokens() {
    LexerOutput output = new LexerOutput();

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    Token commentToken1 = new Token(GenericTokenType.IDENTIFIER, "Word", 10, 4);
    output.addPreprocessingToken(commentToken1);
    Token commentToken2 = new Token(GenericTokenType.IDENTIFIER, "Word", 10, 4);
    output.addPreprocessingToken(commentToken2);
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    Token commentToken3 = new Token(GenericTokenType.IDENTIFIER, "Word", 10, 4);
    output.addPreprocessingToken(commentToken3);
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    Token commentToken4 = new Token(GenericTokenType.IDENTIFIER, "Word", 10, 4);
    output.addPreprocessingToken(commentToken4);

    output.removeLastTokens(2);

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    assertThat(output.size(), is(2));
    assertThat(output.get(0).getTrivia().size(), is(0));
    assertThat(output.get(1).getTrivia().size(), is(4));
    assertThat(output.get(1).getTrivia().get(0), is(commentToken1));
    assertThat(output.get(1).getTrivia().get(1), is(commentToken2));
    assertThat(output.get(1).getTrivia().get(2), is(commentToken3));
    assertThat(output.get(1).getTrivia().get(3), is(commentToken4));
  }

}
