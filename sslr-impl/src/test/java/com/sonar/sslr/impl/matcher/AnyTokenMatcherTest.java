/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.Matchers.anyToken;
import static com.sonar.sslr.test.lexer.TokenUtils.lex;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class AnyTokenMatcherTest {

  @Test
  public void ok() {
    AnyTokenMatcher matcher = new AnyTokenMatcher();
    AstNode node = matcher.match(new ParsingState(lex("print screen")));
    assertEquals("print", node.getTokenValue());

    node = matcher.match(new ParsingState(lex(".")));
    assertEquals(".", node.getTokenValue());
  }
  
  @Test
  public void testToString() {
  	assertEquals(anyToken().toString(), "anyToken()");
  }
  
}
