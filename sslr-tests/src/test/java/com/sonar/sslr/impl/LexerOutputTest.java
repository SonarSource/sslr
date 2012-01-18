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
import com.sonar.sslr.api.Trivia;

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
  public void testNoTrivia() {
    LexerOutput output = new LexerOutput();
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);
    assertThat(output.size(), is(1));
    assertThat(output.get(0).getTrivia().size(), is(0));
  }

  @Test
  public void testAddCommentTrivia() {
    LexerOutput output = new LexerOutput();

    output.addTrivia(Trivia.createCommentTrivia(new Token(GenericTokenType.COMMENT, "comment")));

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    assertThat(output.size(), is(1));
    assertThat(output.get(0).getTrivia().size(), is(1));
    assertThat(output.get(0).getTrivia().get(0).getValue(), is("comment"));
    assertThat(output.get(0).getTrivia().get(0).isComment(), is(true));
    assertThat(output.get(0).getTrivia().get(0).isPreprocessor(), is(false));
  }

  @Test
  public void testTrivaNotAttachedIfTokenBefore() {
    LexerOutput output = new LexerOutput();
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    output.addTrivia(Trivia.createCommentTrivia(new Token(GenericTokenType.COMMENT, "comment")));

    assertThat(output.size(), is(1));
    assertThat(output.get(0).getTrivia().size(), is(0));
  }

  @Test
  public void testTriviaAttachedToRightToken() {
    LexerOutput output = new LexerOutput();

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    output.addTrivia(Trivia.createCommentTrivia(new Token(GenericTokenType.COMMENT, "comment")));
    output.addTrivia(Trivia.createPreprocessorTrivia(new Token(GenericTokenType.IDENTIFIER, "preprocessor")));

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    assertThat(output.size(), is(2));
    assertThat(output.get(0).getTrivia().size(), is(0));
    assertThat(output.get(1).getTrivia().size(), is(2));
    assertThat(output.get(1).getTrivia().get(0).getValue(), is("comment"));
    assertThat(output.get(1).getTrivia().get(0).isComment(), is(true));
    assertThat(output.get(1).getTrivia().get(0).isPreprocessor(), is(false));
    assertThat(output.get(1).getTrivia().get(1).getValue(), is("preprocessor"));
    assertThat(output.get(1).getTrivia().get(1).isComment(), is(false));
    assertThat(output.get(1).getTrivia().get(1).isPreprocessor(), is(true));
  }

  @Test
  public void testTrivaWithRemoveLastTokens() {
    LexerOutput output = new LexerOutput();

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    output.addTrivia(Trivia.createCommentTrivia(new Token(GenericTokenType.COMMENT, "comment1")));
    output.addTrivia(Trivia.createCommentTrivia(new Token(GenericTokenType.COMMENT, "comment2")));
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    output.addTrivia(Trivia.createCommentTrivia(new Token(GenericTokenType.COMMENT, "comment3")));
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    output.addTrivia(Trivia.createCommentTrivia(new Token(GenericTokenType.COMMENT, "comment4")));

    output.removeLastTokens(2);

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "Word", 2, 4);

    assertThat(output.size(), is(2));
    assertThat(output.get(0).getTrivia().size(), is(0));
    assertThat(output.get(1).getTrivia().size(), is(4));
    assertThat(output.get(1).getTrivia().get(0).getValue(), is("comment1"));
    assertThat(output.get(1).getTrivia().get(1).getValue(), is("comment2"));
    assertThat(output.get(1).getTrivia().get(2).getValue(), is("comment3"));
    assertThat(output.get(1).getTrivia().get(3).getValue(), is("comment4"));
  }

}
