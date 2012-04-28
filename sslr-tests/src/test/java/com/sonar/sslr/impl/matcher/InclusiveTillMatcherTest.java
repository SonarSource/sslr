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

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.IdentifierLexer;

public class InclusiveTillMatcherTest {

  @Test
  public void ok() {
    assertThat(till("four"), match("one two three four"));
    assertThat(till("three"), not(match("one two three four")));

    assertThat(till(and("three", "four")), match("one two three four"));
  }

  @Test
  public void testToString() {
    assertEquals(till("(").toString(), "till");
  }

  @Test
  public void testAstNodeTokens() {
    ParsingState state = new ParsingState(IdentifierLexer.create().lex("one two three four"));
    AstNode astNode = till("three").match(state);
    assertEquals(3, state.lexerIndex);
    assertEquals(3, astNode.getChildren().size());
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(till("a") == till("a"), is(true));
    assertThat(till("a") == till("b"), is(false));
    assertThat(till("a") == adjacent("a"), is(false));
  }

}
