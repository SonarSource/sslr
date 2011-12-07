/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
