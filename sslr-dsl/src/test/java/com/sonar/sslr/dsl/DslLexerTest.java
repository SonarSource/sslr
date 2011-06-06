/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import org.junit.Test;

import com.sonar.sslr.dsl.internal.DefaultDslLexer;
import com.sonar.sslr.impl.Lexer;

import static com.sonar.sslr.dsl.DslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DslTokenType.PUNCTUATOR;
import static com.sonar.sslr.dsl.DslTokenType.WORD;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;

import static org.junit.Assert.assertThat;

public class DslLexerTest {

  Lexer lexer = DefaultDslLexer.create();

  @Test
  public void shouldLexIdentifier() {
    assertThat(lexer.lex("my id"), hasToken("my", WORD));
    assertThat(lexer.lex("my id"), hasToken("id", WORD));
  }

  @Test
  public void shouldLexPunctuators() {
    assertThat(lexer.lex("[]"), hasToken("[", PUNCTUATOR));
  }

  @Test
  public void shouldLexConstant() {
    assertThat(lexer.lex("50 bottles"), hasToken("50", INTEGER));
  }

}
