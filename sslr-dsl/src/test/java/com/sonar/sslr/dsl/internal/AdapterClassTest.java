/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AdapterClassTest {

  @Test
  public void shoudHaveMethodWithRequiredArgument() {
    AdapterType adapter = new AdapterType(MyAdapter.class);
    assertThat(adapter.hasMethodWithArgumentType(String.class), is(true));
    assertThat(adapter.hasMethodWithArgumentType(Number.class), is(false));
  }

  @Test
  public void shoudInjectComponent() {
    AdapterType adapter = new AdapterType(MyAdapter.class);
    MyAdapter myAdapter = (MyAdapter) adapter.newInstance();
    adapter.inject(myAdapter, "hello");
    assertThat(myAdapter.message, is("hello"));
  }

  public static class MyAdapter {

    private String message;

    public void setMessage(String message) {
      this.message = message;
    }
  }

  @Test
  public void shoudEquals() {
    assertThat(new AdapterType(String.class), is(new AdapterType(String.class)));
  }

  @Test
  public void shoudNotEquals() {
    assertThat(new AdapterType(String.class), not(is(new AdapterType(Number.class))));
  }

  @Test
  public void shouldHaveTheSameHashcode() {
    assertThat(new AdapterType(String.class).hashCode(), is(new AdapterType(String.class).hashCode()));
  }

  @Test
  public void shouldNotHaveTheSameHashcode() {
    assertThat(new AdapterType(String.class).hashCode(), not(is(new AdapterType(Number.class).hashCode())));
  }
}
