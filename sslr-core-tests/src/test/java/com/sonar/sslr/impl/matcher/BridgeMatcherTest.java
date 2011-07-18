/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.MyPunctuator.CAT;
import static com.sonar.sslr.impl.matcher.MyPunctuator.DOG;
import static com.sonar.sslr.impl.matcher.MyPunctuator.LEFT;
import static com.sonar.sslr.impl.matcher.MyPunctuator.RIGHT;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.IdentifierLexer;

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

  private static List<Token> createTokens(TokenType... types) {
    List<Token> tokens = Lists.newArrayList();
    for (TokenType type : types) {
      tokens.add(new Token(type, type.getValue()));
    }
    return tokens;
  }
  
  @Test
  public void testToString() {
  	assertEquals(bridge(LEFT, RIGHT).toString(), "bridge(LEFT, RIGHT)");
  }
  
  @Test
  public void testAstNodeTokens() {
  	ParsingState state = new ParsingState(IdentifierLexer.create().lex("one ").getTokens());
  	AstNode astNode = bridge(GenericTokenType.IDENTIFIER, GenericTokenType.EOF).match(state);
  	assertEquals(2, state.lexerIndex);
  	assertEquals(2, astNode.getChildren().size());
  }
  
  @Test
  public void testEqualsAndHashCode() {
  	assertThat(bridge(LEFT, RIGHT) == bridge(LEFT, RIGHT), is(true));
  	assertThat(bridge(LEFT, LEFT) == bridge(LEFT, RIGHT), is(false));
  	assertThat(bridge(LEFT, LEFT) == isOneOfThem(LEFT, RIGHT), is(false));
  }
  
}
