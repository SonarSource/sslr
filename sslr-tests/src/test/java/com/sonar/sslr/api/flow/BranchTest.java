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

public class BranchTest {

  @Test
  public void testGrandParentBranch() {
    Branch branch = new Branch();

    assertThat(branch.getDepth(), is(1));
    assertThat(branch.getDepthDetail(), is("1"));
    assertThat(branch.toString(), is("[BRANCH at level 1 : 1]"));
  }

  @Test
  public void shouldHandleDepth() {
    Branch branch = new Branch(new Branch(new Branch()));

    assertThat(branch.getDepth(), is(3));
    assertThat(branch.getDepthDetail(), is("1.1.1"));
    assertThat(branch.toString(), is("[BRANCH at level 3 : 1.1.1]"));

    new Branch(branch);
    new Branch(branch);
    Branch child3 = new Branch(branch);

    assertThat(child3.getDepth(), is(4));
    assertThat(child3.getDepthDetail(), is("1.1.1.3"));
    assertThat(child3.toString(), is("[BRANCH at level 4 : 1.1.1.3]"));
  }

  @Test
  public void shouldBeParentOf() {
    Branch parent = new Branch();
    Branch child11 = new Branch(parent);
    Branch child12 = new Branch(parent);

    assertThat(parent.isParentOf(child11), is(true));
    assertThat(child11.isParentOf(parent), is(false));
    assertThat(child11.isParentOf(child12), is(false));
  }

  @Test
  public void shouldBeParentOrChildOf() {
    Branch parent = new Branch();
    Branch child11 = new Branch(parent);
    Branch child12 = new Branch(parent);

    assertThat(parent.isParentOrChildOf(child11), is(true));
    assertThat(child11.isParentOrChildOf(parent), is(true));
    assertThat(child11.isParentOrChildOf(child12), is(false));
  }
}
