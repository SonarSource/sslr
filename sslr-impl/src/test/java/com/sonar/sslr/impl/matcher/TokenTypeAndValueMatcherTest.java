/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.Matchers.token;
import static com.sonar.sslr.test.lexer.TokenUtils.lex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.ParsingState;

public class TokenTypeAndValueMatcherTest {

  @Test
  public void ok() {
  	TokenTypeAndValueMatcher matcher = new TokenTypeAndValueMatcher(GenericTokenType.IDENTIFIER, "print");
    AstNode node = matcher.match(new ParsingState(lex("print screen")));

    assertTrue(node.is(GenericTokenType.IDENTIFIER));
    assertEquals("print", node.getTokenValue());
  }
  
  @Test
  public void testToString() {
  	assertEquals(token(GenericTokenType.IDENTIFIER, "hello").toString(), "token(IDENTIFIER, \"hello\")");
  }

}
