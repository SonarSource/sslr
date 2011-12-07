/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.ast;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class AlwaysSkipFromAstTest {

  @Test
  public void testHasToBeSkippedFromAst() {
    AlwaysSkipFromAst skipPolicy = new AlwaysSkipFromAst();
    assertThat(skipPolicy.hasToBeSkippedFromAst(null), is(true));
  }

}
