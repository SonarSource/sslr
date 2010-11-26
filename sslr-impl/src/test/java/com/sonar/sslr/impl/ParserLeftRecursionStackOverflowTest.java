/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.sonar.channel.Channel;
import org.sonar.channel.ChannelDispatcher;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.channel.RegexpChannel;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class ParserLeftRecursionStackOverflowTest {

  @Test
  @Ignore
  public void shouldGetLeftRecursionStackOverflow() {
    boolean getRecognitionException = false;
    try {
      MyParser parser = new MyParser();
      parser.parse("something 123");
    } catch (RecognitionExceptionImpl e) {
      getRecognitionException = true;
      assertThat(e.getMessage(), containsString("left recursion"));
    }
    assertThat(getRecognitionException, is(true));
  }

  private static class MyLexer extends Lexer {

    @Override
    protected ChannelDispatcher<LexerOutput> getChannelDispatcher() {
      List<Channel> channels = Lists.newArrayList();
      channels.add(new RegexpChannel(GenericTokenType.IDENTIFIER, "\\w++"));
      channels.add(new RegexpChannel(GenericTokenType.CONSTANT, "\\d++"));
      return new ChannelDispatcher<LexerOutput>(channels);
    }
  }

  private static class MyParser extends Parser<MyGrammar> {

    public MyParser() {
      super(new MyGrammar(), new MyLexer());
    }

  }

  private static class MyGrammar implements Grammar {

    private Rule firstRule = new RuleImpl("firstRule");
    private Rule secondRule = new RuleImpl("secondRule");
    private Rule thirdRule = new RuleImpl("thirdRule");

    public MyGrammar() {
      firstRule.is(GenericTokenType.IDENTIFIER, secondRule);
      secondRule.is(thirdRule);
      thirdRule.is(secondRule);
    }

    public Rule getRootRule() {
      return firstRule;
    }

  }
}
