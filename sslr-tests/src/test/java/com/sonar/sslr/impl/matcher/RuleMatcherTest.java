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

import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.RecognitionExceptionListener;
import com.sonar.sslr.impl.ParsingState;
import org.junit.Before;
import org.junit.Test;

import static com.sonar.sslr.impl.MockTokenType.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.test.lexer.TokenUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuleMatcherTest {

  private RuleDefinition javaClassDefinition;
  private Matcher opMatcher;

  @Before
  public void init() {
    javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    opMatcher = opt("implements", WORD, o2n(",", WORD));
    javaClassDefinition.is("public", or("class", "interface"), opMatcher);
  }

  @Test
  public void getName() {
    assertEquals("JavaClassDefinition", javaClassDefinition.getRule().getName());
  }

  @Test
  public void getToString() {
    assertEquals("JavaClassDefinition", javaClassDefinition.getRule().getName());
  }

  @Test
  public void testNoRecoveryMode() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    ruleBuilder.is("one");

    RuleMatcher rule = ruleBuilder.getRule();

    ParsingState parsingState = new ParsingState(lex("one"));
    RecognitionExceptionListener listener = mock(RecognitionExceptionListener.class);
    parsingState.addListeners(listener);
    rule.match(parsingState);

    verify(listener, times(0)).processRecognitionException(org.mockito.Mockito.any(RecognitionException.class));
  }

  @Test
  public void testRecoveryModeListenerInvoked() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    ruleBuilder.is("one");

    RuleMatcher rule = ruleBuilder.getRule();
    rule.recoveryRule();

    ParsingState parsingState = new ParsingState(lex("one"));
    RecognitionExceptionListener listener = mock(RecognitionExceptionListener.class);
    parsingState.addListeners(listener);
    rule.match(parsingState);

    verify(listener, times(1)).processRecognitionException(org.mockito.Mockito.any(RecognitionException.class));
  }

  @Test
  public void testRecoveryModeAtErrorLexerIndex() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    ruleBuilder.is("one");

    RuleMatcher rule = ruleBuilder.getRule();
    rule.recoveryRule();

    final ParsingState parsingState = new ParsingState(lex("one"));

    RecognitionExceptionListener listener = new RecognitionExceptionListener() {

      public void processRecognitionException(RecognitionException e) {
        assertThat(parsingState.lexerIndex, is(0));
      }

    };
    parsingState.addListeners(listener);
    rule.match(parsingState);

    assertThat(parsingState.lexerIndex, is(1));
  }

}
