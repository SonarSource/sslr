/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
