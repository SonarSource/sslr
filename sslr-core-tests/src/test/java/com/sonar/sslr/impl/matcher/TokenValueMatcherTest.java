/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.test.lexer.TokenUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class TokenValueMatcherTest {

  @Test
  public void ok() {
    TokenValueMatcher matcher = new TokenValueMatcher("print");
    AstNode node = matcher.match(new ParsingState(lex("print screen")));

    assertEquals("print", node.getTokenValue());
  }

  @Test
  public void testToString() {
    assertEquals(new TokenValueMatcher("print").toString(), "\"print\"");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(and("hehe") == and("hehe"), is(true));
    assertThat(and("hehe") == and("haha"), is(false));
    assertThat(and("hehe") == adjacent("hehe"), is(false));
  }

}
