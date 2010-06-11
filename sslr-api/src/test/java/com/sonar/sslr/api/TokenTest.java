/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

public class TokenTest {

  @Test
  public void testCopy() {
    Token originalToken = new Token(GenericTokenType.IDENTIFIER, "word", 2, 5, new File("/myFilePath"));
    Token templateToken = new Token(GenericTokenType.LITERAL, "'value'", 12, 15, new File("/myTemplateFilePath"));

    Token clone = originalToken.copy(templateToken);
    assertThat((GenericTokenType) clone.getType(), is(GenericTokenType.IDENTIFIER));
    assertThat(clone.getValue(), is("word"));
    assertThat(clone.getLine(), is(12));
    assertThat(clone.getColumn(), is(15));
    assertThat(clone.getFile().getAbsolutePath(), is("/myTemplateFilePath"));
  }

}
