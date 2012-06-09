/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import org.junit.Test;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.adjacent;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.test.lexer.TokenUtils.lex;
import static org.fest.assertions.Assertions.assertThat;

public class TokenTypeMatcherTest {

  @Test
  public void ok() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(IDENTIFIER);
    AstNode node = matcher.match(new ParsingState(lex("print screen")));

    assertThat(node.is(IDENTIFIER)).isTrue();
  }

  @Test
  public void testToString() {
    assertThat(and(IDENTIFIER).toString()).isEqualTo("IDENTIFIER");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(and(IDENTIFIER) == and(IDENTIFIER)).isTrue();
    assertThat(and(IDENTIFIER) == and(EOF)).isFalse();
    assertThat(and(IDENTIFIER) == adjacent("(")).isFalse();
  }

}
