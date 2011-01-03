/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BlockTest {

  Block block = new Block();

  @Test
  public void testIndexOf() {
    Statement myStmt = new Statement(null);
    block.addStatement(new Statement(null));
    block.addStatement(myStmt);
    block.addStatement(new Statement(null));
    assertThat(block.indexOf(null), is( -1));
    assertThat(block.indexOf(new Statement(null)), is( -1));
    assertThat(block.indexOf(myStmt), is(1));
  }
  
  @Test
  public void shouldGetLastStatement() {
    assertThat(block.getLastStatement(), is(nullValue()));
    
    block.addStatement(new Statement(null));
    block.addStatement(new Statement(null));
    Statement myStmt = new Statement(null);
    block.addStatement(myStmt);
    assertThat(block.getLastStatement(), is(myStmt));
  }

}
