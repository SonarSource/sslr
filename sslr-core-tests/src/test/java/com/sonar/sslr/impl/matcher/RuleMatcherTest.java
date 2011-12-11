/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.MockTokenType.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.test.lexer.TokenUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.RecognitionExceptionListener;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.ParsingState;

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
  public void testRecoveryMode() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    ruleBuilder.is("one");

    RuleMatcher rule = ruleBuilder.getRule();

    ParsingState parsingState = new ParsingState(lex("one"));
    RecognitionExceptionListener listener = mock(RecognitionExceptionListener.class);
    parsingState.addListener(listener);
    rule.match(parsingState);

    verify(listener, times(0)).processRecognitionException((RecognitionException) anyObject());

    rule.recoveryRule();
    parsingState = new ParsingState(lex("one"));
    listener = mock(RecognitionExceptionListener.class);
    parsingState.addListener(listener);
    rule.reinitializeMatcherTree();
    rule.match(parsingState);

    verify(listener, times(1)).processRecognitionException((RecognitionException) anyObject());
  }
}
