/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.nio.charset.Charset;

import org.junit.Test;

import com.sonar.sslr.api.Preprocessor;

public class LexerTest {

  @Test
  public void defaultCharsetTest() {
    Lexer lexer = Lexer.builder().build();
    assertThat(lexer.getCharset(), is(Charset.defaultCharset()));
  }

  @Test
  public void specificCharsetTest() {
    Charset charset = mock(Charset.class);
    Lexer lexer = Lexer.builder().withCharset(charset).build();
    assertThat(lexer.getCharset(), is(charset));
  }

  @Test
  public void preprocessorsTest() {
    Preprocessor p1 = mock(Preprocessor.class);
    Preprocessor p2 = mock(Preprocessor.class);

    Lexer lexer = Lexer.builder()
        .withPreprocessor(p1)
        .withPreprocessor(p2)
        .build();

    assertThat(lexer.getPreprocessors().length, is(2));
    assertThat(lexer.getPreprocessors()[0], is(p1));
    assertThat(lexer.getPreprocessors()[1], is(p2));
  }

}
