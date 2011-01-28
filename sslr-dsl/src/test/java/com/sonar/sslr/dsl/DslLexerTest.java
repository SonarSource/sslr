/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import static com.sonar.sslr.dsl.DslPunctuator.LBRACKET;
import static com.sonar.sslr.dsl.DslTokenType.EOL;
import static com.sonar.sslr.dsl.DslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DslTokenType.WORD;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.dsl.internal.DefaultDslLexer;

public class DslLexerTest {

  DefaultDslLexer lexer = new DefaultDslLexer();

  @Test
  public void shouldLexIdentifier() {
    assertThat(lexer.lex("my id"), hasToken("my", WORD));
    assertThat(lexer.lex("my id"), hasToken("id", WORD));
  }
  
  @Test
  public void shouldLexEol() {
    assertThat(lexer.lex("first line\n"), hasToken("\n", EOL));
  }
  
  @Test
  public void shouldLexPunctuators() {
    assertThat(lexer.lex("[]"), hasToken("[", LBRACKET));
  }
  
  @Test
  public void shouldLexConstant() {
    assertThat(lexer.lex("50 bottles"), hasToken("50", INTEGER));
  }

}
