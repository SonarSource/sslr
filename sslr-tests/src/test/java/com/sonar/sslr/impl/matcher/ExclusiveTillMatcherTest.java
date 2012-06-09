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

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.adjacent;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.exclusiveTill;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThat;

public class ExclusiveTillMatcherTest {

  @Test
  public void ok() {
    assertThat(and(exclusiveTill("four"), "four"), match("one two three four"));
    assertThat(and(exclusiveTill("two", "three"), "two", "three", "four"), match("one two three four"));
    assertThat(and(exclusiveTill("two", "three"), "three", "two", "four"), match("one three two four"));
  }

  @Test
  public void testToString() {
    assertThat(exclusiveTill("(").toString()).isEqualTo("exclusiveTill");
  }

  @Test
  public void testAstNodeTokens() {
    ParsingState state = new ParsingState(IdentifierLexer.create().lex("one two three four"));
    AstNode astNode = exclusiveTill("three").match(state);
    assertThat(state.lexerIndex).isEqualTo(2);
    assertThat(astNode.getChildren().size()).isEqualTo(2);
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(exclusiveTill("a", "a") == exclusiveTill("a", "a")).isTrue();
    assertThat(exclusiveTill("a", "a") == exclusiveTill("a", "b")).isFalse();
    assertThat(exclusiveTill("a", "a") == adjacent("a")).isFalse();
  }

}
