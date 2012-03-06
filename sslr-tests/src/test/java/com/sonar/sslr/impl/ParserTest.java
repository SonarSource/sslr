/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.miniC.MiniCParser.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.RecognitionException;

public class ParserTest {

  @Test(expected = RecognitionException.class)
  public void lexerErrorStringWrappedInRecognitionException() {
    parseString(".");
  }

  @Test(expected = RecognitionException.class)
  public void lexerErrorFileWrappedInRecognitionException() {
    parseFile("/OwnExamples/lexererror.mc");
  }

  @Test
  public void lexerErrorNotWorthToRetry() {
    try {
      parseString(".");
      throw new AssertionError("This should be unreachable!");
    } catch (RecognitionException re) {
      assertThat(re.isToRetryWithExtendStackTrace(), is(false));
    }
  }

  @Test
  public void parseErrorWorthToRetry() {
    try {
      parseString("<");
      throw new AssertionError("This should be unreachable!");
    } catch (RecognitionException re) {
      assertThat(re.isToRetryWithExtendStackTrace(), is(true));
    }
  }

  @Test
  public void parse() {
    AstNode compilationUnit = parseString("");
    assertThat(compilationUnit.getNumberOfChildren(), is(1));
    assertThat(compilationUnit.getChild(0).is(EOF), is(true));
  }

}
