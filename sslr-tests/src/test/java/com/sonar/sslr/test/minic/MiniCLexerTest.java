/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.test.minic;

import com.sonar.sslr.impl.Lexer;
import org.junit.Test;

import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static com.sonar.sslr.test.minic.MiniCLexer.Keywords.*;
import static com.sonar.sslr.test.minic.MiniCLexer.Literals.INTEGER;
import static com.sonar.sslr.test.minic.MiniCLexer.Punctuators.*;
import static org.junit.Assert.assertThat;

public class MiniCLexerTest {

  Lexer lexer = MiniCLexer.create();

  @Test
  public void lexIdentifiers() {
    assertThat(lexer.lex("abc"), hasToken("abc", IDENTIFIER));
    assertThat(lexer.lex("abc0"), hasToken("abc0", IDENTIFIER));
    assertThat(lexer.lex("abc_0"), hasToken("abc_0", IDENTIFIER));
    assertThat(lexer.lex("i"), hasToken("i", IDENTIFIER));
  }

  @Test
  public void lexIntegers() {
    assertThat(lexer.lex("0"), hasToken("0", INTEGER));
    assertThat(lexer.lex("000"), hasToken("000", INTEGER));
    assertThat(lexer.lex("1234"), hasToken("1234", INTEGER));
  }

  @Test
  public void lexKeywords() {
    assertThat(lexer.lex("int"), hasToken(INT));
    assertThat(lexer.lex("void"), hasToken(VOID));
    assertThat(lexer.lex("return"), hasToken(RETURN));
    assertThat(lexer.lex("if"), hasToken(IF));
    assertThat(lexer.lex("else"), hasToken(ELSE));
    assertThat(lexer.lex("while"), hasToken(WHILE));
    assertThat(lexer.lex("break"), hasToken(BREAK));
    assertThat(lexer.lex("continue"), hasToken(CONTINUE));
    assertThat(lexer.lex("struct"), hasToken(STRUCT));
  }

  @Test
  public void lexComments() {
    assertThat(lexer.lex("/*test*/"), hasComment("/*test*/"));
    assertThat(lexer.lex("/*test*/*/"), hasComment("/*test*/"));
    assertThat(lexer.lex("/*test/* /**/"), hasComment("/*test/* /**/"));
    assertThat(lexer.lex("/*test1\ntest2\ntest3*/"), hasComment("/*test1\ntest2\ntest3*/"));
  }

  @Test
  public void lexPunctuators() {
    assertThat(lexer.lex("("), hasToken(PAREN_L));
    assertThat(lexer.lex(")"), hasToken(PAREN_R));
    assertThat(lexer.lex("{"), hasToken(BRACE_L));
    assertThat(lexer.lex("}"), hasToken(BRACE_R));
    assertThat(lexer.lex("="), hasToken(EQ));
    assertThat(lexer.lex(","), hasToken(COMMA));
    assertThat(lexer.lex(";"), hasToken(SEMICOLON));
    assertThat(lexer.lex("+"), hasToken(ADD));
    assertThat(lexer.lex("-"), hasToken(SUB));
    assertThat(lexer.lex("*"), hasToken(MUL));
    assertThat(lexer.lex("/"), hasToken(DIV));
    assertThat(lexer.lex("<"), hasToken(LT));
    assertThat(lexer.lex("<="), hasToken(LTE));
    assertThat(lexer.lex(">"), hasToken(GT));
    assertThat(lexer.lex(">="), hasToken(GTE));
    assertThat(lexer.lex("=="), hasToken(EQEQ));
    assertThat(lexer.lex("!="), hasToken(NE));
    assertThat(lexer.lex("++"), hasToken(INC));
    assertThat(lexer.lex("--"), hasToken(DEC));
  }

}
