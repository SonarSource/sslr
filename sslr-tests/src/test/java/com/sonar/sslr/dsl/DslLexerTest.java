/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import static com.sonar.sslr.dsl.DslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.dsl.DslTokenType.PUNCTUATOR;
import static com.sonar.sslr.dsl.DslTokenType.WORD;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.dsl.internal.DefaultDslLexer;
import com.sonar.sslr.impl.Lexer;

public class DslLexerTest {

  Lexer lexer = DefaultDslLexer.create();

  @Test
  public void shouldLexWord() {
    assertThat(lexer.lex("my id"), hasToken("my", WORD));
    assertThat(lexer.lex("my id2"), hasToken("id2", WORD));
    assertThat(lexer.lex("My_id"), hasToken("My_id", WORD));
  }

  @Test
  public void shouldLexLiteral() {
    assertThat(lexer.lex("'literal' 'literal' something"), hasToken("'literal'", LITERAL));
    assertThat(lexer.lex("'lit\\'eral' something"), hasToken("'lit\\'eral'", LITERAL));
    assertThat(lexer.lex("'\\'TOTO\\'' something"), hasToken("'\\'TOTO\\''", LITERAL));
    assertThat(lexer.lex("\"literal\" something"), hasToken("\"literal\"", LITERAL));
    assertThat(lexer.lex("\"lit\\\"eral\" something"), hasToken("\"lit\\\"eral\"", LITERAL));
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
