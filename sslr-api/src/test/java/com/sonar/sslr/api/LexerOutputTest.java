/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class LexerOutputTest {

  LexerOutput output;

  @Test
  public void shouldAddToken() {
    output = new LexerOutput();
    assertThat(output.getTokens().size(), is(0));
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "myIdentifier", 0, 0);
    assertThat(output.getTokens().size(), is(1));
    assertThat(output.get(0).getValue(), is("myIdentifier"));
  }

  @Test
  public void shouldAddTokenAndNotifyPreprocessors() {
    Preprocessor firstPreprocessors = mock(Preprocessor.class);
    Preprocessor secondPreprocessors = mock(Preprocessor.class);
    output = new LexerOutput(firstPreprocessors, secondPreprocessors);

    when(firstPreprocessors.process((Token) anyObject(), eq(output))).thenReturn(false);
    when(secondPreprocessors.process((Token) anyObject(), eq(output))).thenReturn(false);

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "firstIdentifier", 0, 0);
    assertThat(output.getTokens().size(), is(1));

    verify(firstPreprocessors).process(output.get(0), output);
    verify(secondPreprocessors).process(output.get(0), output);
  }

  @Test
  public void shouldAddTokenAndNotifyOnlyTheFirstPreprocessor() {
    Preprocessor firstPreprocessors = mock(Preprocessor.class);
    Preprocessor secondPreprocessors = mock(Preprocessor.class);
    output = new LexerOutput(firstPreprocessors, secondPreprocessors);

    when(firstPreprocessors.process((Token) anyObject(), eq(output))).thenReturn(true);
    when(secondPreprocessors.process((Token) anyObject(), eq(output))).thenReturn(false);

    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "firstIdentifier", 0, 0);
    assertThat(output.getTokens().size(), is(0));

    verify(secondPreprocessors, times(0)).process((Token) anyObject(), eq(output));
  }

  @Test
  public void shouldGetNullWhenRequestingLastTokenOnEmptyLexerOutput() {
    output = new LexerOutput();
    assertThat(output.getLastToken(), is(nullValue()));
  }

  @Test
  public void shouldGetFirstAddedToken() {
    output = new LexerOutput();
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "firstIdentifier", 0, 0);
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "secondIdentifier", 0, 0);
    assertThat(output.getFirstToken().getValue(), is("firstIdentifier"));
  }

  @Test
  public void shouldGetLastAddedToken() {
    output = new LexerOutput();
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "firstIdentifier", 0, 0);
    output.addTokenAndProcess(GenericTokenType.IDENTIFIER, "secondIdentifier", 0, 0);
    assertThat(output.getLastToken().getValue(), is("secondIdentifier"));
  }

  @Test
  public void pushingBackTokenShouldNotCallBackThePreprocessor() {
    Preprocessor pushBackPressprocessor = mock(Preprocessor.class);
    Preprocessor secondPreprocessors = mock(Preprocessor.class);
    output = new LexerOutput(pushBackPressprocessor, secondPreprocessors);

    when(pushBackPressprocessor.process((Token) anyObject(), eq(output))).thenReturn(false);
    when(secondPreprocessors.process((Token) anyObject(), eq(output))).thenReturn(false);

    output.pushBackTokenAndProcess(new Token(GenericTokenType.IDENTIFIER, "firstIdentifier"), pushBackPressprocessor);

    assertThat(output.getTokens().size(), is(1));

    verify(pushBackPressprocessor, times(0)).process((Token) anyObject(), eq(output));
    verify(secondPreprocessors, times(1)).process((Token) anyObject(), eq(output));
  }

  @Test
  public void pushingBackTokensShouldNotCallBackThePreprocessor() {
    Preprocessor pushBackPressprocessor = mock(Preprocessor.class);
    Preprocessor secondPreprocessors = mock(Preprocessor.class);
    output = new LexerOutput(pushBackPressprocessor, secondPreprocessors);

    when(pushBackPressprocessor.process((Token) anyObject(), eq(output))).thenReturn(false);
    when(secondPreprocessors.process((Token) anyObject(), eq(output))).thenReturn(false);

    List<Token> tokensToPushBack = Lists.newArrayList(new Token(GenericTokenType.IDENTIFIER, "firstIdentifier"), new Token(
        GenericTokenType.IDENTIFIER, "secondIdentifier"));

    output.pushBackTokensAndProcess(tokensToPushBack, pushBackPressprocessor);

    assertThat(output.getTokens().size(), is(2));

    verify(pushBackPressprocessor, times(0)).process((Token) anyObject(), eq(output));
    verify(secondPreprocessors, times(2)).process((Token) anyObject(), eq(output));
  }

}
