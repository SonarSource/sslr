/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.anyToken;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.till;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.tillNewLine;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
  	ParsingState state = new ParsingState(IdentifierLexer.create().lex("one two three\nfour").getTokens());
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
