/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
    ParsingState state = new ParsingState(IdentifierLexer.create().lex("one two three four").getTokens());
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
