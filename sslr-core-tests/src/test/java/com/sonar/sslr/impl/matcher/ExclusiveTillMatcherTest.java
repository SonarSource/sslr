/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.IdentifierLexer;

public class ExclusiveTillMatcherTest {

  @Test
  public void ok() {
    assertThat(and(exclusiveTill("four"), "four"), match("one two three four"));
    assertThat(and(exclusiveTill("two", "three"), "two", "three", "four"), match("one two three four"));
    assertThat(and(exclusiveTill("two", "three"), "three", "two", "four"), match("one three two four"));
  }
  
  @Test
  public void testToString() {
  	assertEquals(exclusiveTill("(").toString(), "exclusiveTill");
  }
  
  @Test
  public void testAstNodeTokens() {
  	ParsingState state = new ParsingState(IdentifierLexer.create().lex("one two three four").getTokens());
  	exclusiveTill("three").reinitializeMatcherTree();
  	AstNode astNode = exclusiveTill("three").match(state);
  	assertEquals(2, state.lexerIndex);
  	assertEquals(2, astNode.getChildren().size());
  }
  
  @Test
  public void testEqualsAndHashCode() {
  	assertThat(exclusiveTill("a", "a") == exclusiveTill("a", "a"), is(true));
  	assertThat(exclusiveTill("a", "a") == exclusiveTill("a", "b"), is(false));
  	assertThat(exclusiveTill("a", "a") == adjacent("a"), is(false));
  }

}
