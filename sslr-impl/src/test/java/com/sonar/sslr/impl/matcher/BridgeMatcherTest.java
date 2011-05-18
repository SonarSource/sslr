/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.bridge;
import static com.sonar.sslr.impl.matcher.MyPunctuator.*;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public class BridgeMatcherTest {
	
	@Test
  public void shouldMatchSimpleBridge() {
    assertThat(bridge(LEFT, RIGHT), match(createTokens(LEFT, CAT, CAT, DOG, RIGHT)));
  }

  @Test
  public void shouldMatchCompositeBridges() {
    assertThat(bridge(LEFT, RIGHT), match(createTokens(LEFT, LEFT, CAT, LEFT, RIGHT, DOG, RIGHT, RIGHT)));
  }

  @Test
  public void shouldNotMatchBridgeStarter() {
    assertThat(bridge(LEFT, RIGHT), not(match(createTokens(CAT, LEFT, RIGHT))));
  }

  @Test
  public void shouldNotMatchPartialBridge() {
    assertThat(bridge(LEFT, RIGHT), not(match(createTokens(LEFT, LEFT, RIGHT))));
  }

  @Test
  public void testGetDefinition() {
    assertEquals("bridge(LEFT, RIGHT)", bridge(LEFT, RIGHT).getDefinition());
  }

  private static List<Token> createTokens(TokenType... types) {
    List<Token> tokens = Lists.newArrayList();
    for (TokenType type : types) {
      tokens.add(new Token(type, type.getValue()));
    }
    return tokens;
  }
}
