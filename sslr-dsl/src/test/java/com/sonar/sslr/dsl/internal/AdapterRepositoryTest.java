/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AdapterRepositoryTest {

  AdapterRepository adapters = new AdapterRepository();

  public static class MyAdapter {

    private String message;
    private MyAdapter child;

    public void setMessage(String message) {
      this.message = message;
    }

    public void setChild(MyAdapter child) {
      this.child = child;
    }

  }

  @Test
  public void shouldInjectAdapter() {
    MyAdapter parentAdapter = (MyAdapter) adapters.plug(MyAdapter.class, null);
    MyAdapter childAdapter = (MyAdapter) adapters.plug(MyAdapter.class, null);

    adapters.injectAdapter(parentAdapter, childAdapter);

    assertThat(parentAdapter.child, is(childAdapter));

  }
}
