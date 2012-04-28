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

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.test.lexer.TokenUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class TokenTypeMatcherTest {

  @Test
  public void ok() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(IDENTIFIER);
    AstNode node = matcher.match(new ParsingState(lex("print screen")));

    assertTrue(node.is(IDENTIFIER));
  }

  @Test
  public void testToString() {
    assertEquals(and(IDENTIFIER).toString(), "IDENTIFIER");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(and(IDENTIFIER) == and(IDENTIFIER), is(true));
    assertThat(and(IDENTIFIER) == and(EOF), is(false));
    assertThat(and(IDENTIFIER) == adjacent("("), is(false));
  }

}
