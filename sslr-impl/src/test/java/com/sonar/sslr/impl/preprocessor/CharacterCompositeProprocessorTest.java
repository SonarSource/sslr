/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.preprocessor;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.LexerOutput;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CharacterCompositeProprocessorTest {

  private CharacterCompositeProprocessor preprocessor = new CharacterCompositeProprocessor(MyCharacterComposite.values());
  private LexerOutput output;

  @Before
  public void init() {
    preprocessor = new CharacterCompositeProprocessor(MyCharacterComposite.values());
    output = new LexerOutput(preprocessor);
  }

  @Test
  public void testProcessEQ_OP() {
    output.addToken(MyCharacter.EQUAL, "=", 1, 7);
    output.addToken(MyCharacter.EQUAL, "=", 1, 8);
    output.addToken(GenericTokenType.WORD, "word", 1, 9);

    assertThat(output.size(), is(2));
    assertThat(output.get(0).getValue(), is("=="));
    assertThat(output.get(0).getLine(), is(1));
    assertThat(output.get(0).getColumn(), is(7));
    assertThat(output.get(1).getValue(), is("word"));
  }

  @Test
  public void testProcessNE_OP() {
    output.addToken(MyCharacter.EQUAL, "!", 1, 7);
    output.addToken(MyCharacter.EQUAL, "=", 1, 8);
    output.addToken(GenericTokenType.WORD, "word", 1, 9);

    assertThat(output.size(), is(2));
    assertThat(output.get(0).getValue(), is("!="));
    assertThat(output.get(1).getValue(), is("word"));
  }
  
  @Test
  public void testProcessCommaBeforeNE_OP() {
    output.addToken(MyCharacter.EQUAL, ",", 1, 7);
    output.addToken(MyCharacter.EQUAL, "!", 1, 7);
    output.addToken(MyCharacter.EQUAL, "=", 1, 8);
    output.addToken(GenericTokenType.WORD, "word", 1, 9);

    assertThat(output.size(), is(3));
    assertThat(output.get(0).getValue(), is(","));
    assertThat(output.get(1).getValue(), is("!="));
    assertThat(output.get(2).getValue(), is("word"));
  }

  @Test
  public void testProcessEqualChar() {
    output.addToken(MyCharacter.EQUAL, "=", 1, 8);
    output.addToken(GenericTokenType.WORD, "word", 3, 4);

    assertThat(output.size(), is(2));
    assertThat(output.getLastToken().getValue(), is("word"));
  }
}
