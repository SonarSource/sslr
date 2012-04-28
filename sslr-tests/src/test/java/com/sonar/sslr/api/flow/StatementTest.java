/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
