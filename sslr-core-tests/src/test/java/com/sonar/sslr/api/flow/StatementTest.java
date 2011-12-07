/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class StatementTest {

  Statement stmt = new Statement(null);

  @Test
  public void shouldNotHaveNext() {
    assertThat(stmt.hasNext(), is(false));
    stmt.setNext(null);
    assertThat(stmt.hasNext(), is(false));
  }

  @Test
  public void shouldHaveNext() {
    stmt.setNext(new Statement(null));
    assertThat(stmt.hasNext(), is(true));
  }

  @Test
  public void shouldNotHavePrevious() {
    assertThat(stmt.hasPrevious(), is(false));
  }

  @Test
  public void shouldHavePrevious() {
    Statement previous = new Statement(null);
    previous.setNext(stmt);
    assertThat(stmt.hasPrevious(), is(true));
    assertThat(stmt.getPrevious(), is(previous));
  }

}
