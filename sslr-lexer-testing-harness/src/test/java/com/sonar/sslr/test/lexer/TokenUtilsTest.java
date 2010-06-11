/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.test.lexer.TokenUtils.merge;
import static com.sonar.sslr.test.lexer.TokenUtils.split;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sonar.sslr.api.Token;

public class TokenUtilsTest {

  @Test
  public void testMerge() {
    List<Token> tokens = new ArrayList<Token>();
    tokens.add(new Token(IDENTIFIER, "a"));
    tokens.add(new Token(IDENTIFIER, "*"));
    tokens.add(new Token(IDENTIFIER, "b"));
    tokens.add(new Token(IDENTIFIER, ";"));

    assertThat(merge(tokens), is("a * b ;"));
  }

  @Test
  public void testSplit() {
    List<Token> tokens = split("myMacro(a, param2)");
    assertThat(merge(tokens), is("myMacro ( a , param2 )"));
  }

}
