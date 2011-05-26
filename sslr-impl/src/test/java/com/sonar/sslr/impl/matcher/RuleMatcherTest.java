/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.MockTokenType.WORD;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Standard.o2n;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Standard.or;
import static com.sonar.sslr.test.lexer.TokenUtils.lex;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.AstListener;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.RecognictionExceptionListener;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;

public class RuleMatcherTest {

  private RuleBuilder javaClassDefinition;
  private Matcher opMatcher;

  @Before
  public void init() {
    javaClassDefinition = RuleBuilder.newRuleBuilder("JavaClassDefinition");
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
  public void testSetAstNodeListener() {
    RuleMatcher rule = new RuleMatcher("MyRule");
    AstListener listener = mock(AstListener.class);
    ParsingState parsingState = mock(ParsingState.class);
    Object output = mock(Object.class);

    rule.setListener(listener);
    rule.setMatcher(new BooleanMatcher(true));
    AstNode node = rule.match(parsingState);
    node.startListening(output);

    verify(listener).startListening(node, output);
  }

  @Test
  public void testSkipFromAst() {
    RuleBuilder ruleBuilder = RuleBuilder.newRuleBuilder("MyRule");
    RuleMatcher rule = ruleBuilder.getRule();
    assertThat(rule.hasToBeSkippedFromAst(null), is(false));

    ruleBuilder.skip();
    assertThat(rule.hasToBeSkippedFromAst(null), is(true));
  }

  @Test
  public void testSkipFromAstIf() {
    RuleMatcher rule = new RuleMatcher("MyRule");
    rule.skipIf(new SkipFromAstIfOnlyOneChild());

    AstNode parent = new AstNode(new Token(GenericTokenType.IDENTIFIER, "parent"));
    AstNode child1 = new AstNode(new Token(GenericTokenType.IDENTIFIER, "child1"));
    AstNode child2 = new AstNode(new Token(GenericTokenType.IDENTIFIER, "child2"));
    parent.addChild(child1);
    parent.addChild(child2);
    child1.addChild(child2);

    assertThat(rule.hasToBeSkippedFromAst(parent), is(false));
    assertThat(rule.hasToBeSkippedFromAst(child2), is(false));
    assertThat(rule.hasToBeSkippedFromAst(child1), is(true));
  }

  @Test
  public void testRecoveryMode() {
    RuleBuilder ruleBuilder = RuleBuilder.newRuleBuilder("MyRule");
    ruleBuilder.is("one");

    RuleMatcher rule = ruleBuilder.getRule();

    ParsingState parsingState = new ParsingState(lex("one"));
    RecognictionExceptionListener listener = mock(RecognictionExceptionListener.class);
    parsingState.addListener(listener);
    rule.match(parsingState);

    verify(listener, times(0)).addRecognitionException((RecognitionException) anyObject());

    rule.recoveryRule();
    parsingState = new ParsingState(lex("one"));
    listener = mock(RecognictionExceptionListener.class);
    parsingState.addListener(listener);
    rule.match(parsingState);

    verify(listener, times(1)).addRecognitionException((RecognitionException) anyObject());
  }
}
