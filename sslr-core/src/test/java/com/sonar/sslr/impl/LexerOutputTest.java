/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import org.junit.Test;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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

}
