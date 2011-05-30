/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.ast;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NeverSkipFromAstTest {

  @Test
  public void testHasToBeSkippedFromAst() {
    NeverSkipFromAst skipPolicy = new NeverSkipFromAst();
    assertThat(skipPolicy.hasToBeSkippedFromAst(null), is(false));
  }

}
