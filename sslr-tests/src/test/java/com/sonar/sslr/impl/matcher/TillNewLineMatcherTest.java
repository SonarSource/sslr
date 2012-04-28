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
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.IdentifierLexer;

public class TillNewLineMatcherTest {

  @Test
  public void ok() {
    assertThat(tillNewLine(), match(""));
    assertThat(tillNewLine(), match("   "));
    assertThat(tillNewLine(), match("a b c d"));
    assertThat(and(tillNewLine(), "new"), match("a b c d \n new"));
    assertThat(and(tillNewLine(), "new"), match("a b c d \n\n new"));
    assertThat(and(tillNewLine(), "new"), match("a b c d \n\n\n new"));
    assertThat(and(tillNewLine(), "a", "b", "c"), match("\n a b c"));
    assertThat(and("a", "b", "c", tillNewLine()), match("a b c"));
    assertThat(and("a", "b", "c", tillNewLine()), match("a b c \n"));
    assertThat(and("a", "b", "c", tillNewLine()), match("a b c \n\n"));
    assertThat(and("bonjour", tillNewLine(), "hehe"), match("\n\n\nbonjour whatever wtf \n hehe"));
    assertThat(and("bonjour", tillNewLine()), match("bonjour hehe hoho huhu"));
    assertThat(and("bonjour", tillNewLine(), EOF), match("bonjour hehe hoho huhu EOF"));
  }

  @Test
  public void testToString() {
    assertEquals(tillNewLine().toString(), "tillNewLine()");
  }

  @Test
  public void testAstNodeTokens() {
    ParsingState state = new ParsingState(IdentifierLexer.create().lex("one two three\nfour"));
    AstNode astNode = tillNewLine().match(state);
    assertEquals(3, state.lexerIndex);
    assertEquals(3, astNode.getChildren().size());
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(tillNewLine() == tillNewLine(), is(true));
    assertThat(tillNewLine() == anyToken(), is(false));
  }

}
