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

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.IdentifierLexer;
import org.junit.Test;

import java.util.List;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static com.sonar.sslr.impl.matcher.MyPunctuator.*;
import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
      tokens.add(mockToken(type, type.getValue()));
    }
    return tokens;
  }

  @Test
  public void testToString() {
    assertEquals(bridge(LEFT, RIGHT).toString(), "bridge(LEFT, RIGHT)");
  }

  @Test
  public void testAstNodeTokens() {
    ParsingState state = new ParsingState(IdentifierLexer.create().lex("one "));
    AstNode astNode = bridge(GenericTokenType.IDENTIFIER, GenericTokenType.EOF).match(state);
    assertEquals(2, state.lexerIndex);
    assertEquals(2, astNode.getChildren().size());
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(bridge(LEFT, RIGHT) == bridge(LEFT, RIGHT), is(true));
    assertThat(bridge(LEFT, LEFT) == bridge(LEFT, RIGHT), is(false));
    assertThat(bridge(LEFT, LEFT) == and(LEFT, RIGHT), is(false));
  }

}
