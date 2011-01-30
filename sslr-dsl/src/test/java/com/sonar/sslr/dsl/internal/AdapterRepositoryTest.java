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

  @Test
  public void shoudInjectComponent() {
    MyAdapter myAdapter = (MyAdapter) adapters.newInstance(MyAdapter.class);
    adapters.inject("hello");
    assertThat(myAdapter.message, is("hello"));
  }

  public static class MyAdapter {

    private String message;

    public void setMessage(String message) {
      this.message = message;
    }
  }
}
