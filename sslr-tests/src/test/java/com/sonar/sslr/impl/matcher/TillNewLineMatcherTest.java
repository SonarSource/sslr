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
import com.sonar.sslr.impl.events.IdentifierLexer;
import org.junit.Test;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.anyToken;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.tillNewLine;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThat;

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
    assertThat(tillNewLine().toString()).isEqualTo("tillNewLine()");
  }

  @Test
  public void testAstNodeTokens() {
    ParsingState state = new ParsingState(IdentifierLexer.create().lex("one two three\nfour"));
    AstNode astNode = tillNewLine().match(state);
    assertThat(state.lexerIndex).isEqualTo(3);
    assertThat(astNode.getChildren().size()).isEqualTo(3);
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(tillNewLine() == tillNewLine()).isTrue();
    assertThat(tillNewLine() == anyToken()).isFalse();
  }

}
