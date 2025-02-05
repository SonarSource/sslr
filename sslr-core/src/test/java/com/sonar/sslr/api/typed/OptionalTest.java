/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.api.typed;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class OptionalTest {

  private final Optional<String> present = Optional.of("foo");
  private final Optional<String> absent = Optional.absent();

  @Test
  public void present() {
    assertThat(present.isPresent()).isTrue();

    assertThat(present.orNull()).isSameAs("foo");

    assertThat(present.or("bar")).isSameAs("foo");

    assertThat(present.get()).isSameAs("foo");

    assertThat(present.toString()).isEqualTo("Optional.of(foo)");

    assertThat(present.equals(present)).isTrue();
    assertThat(present.equals(Optional.of("foo"))).isTrue();
    assertThat(present.equals(Optional.of("bar"))).isFalse();
    assertThat(present.equals(absent)).isFalse();

    assertThat(present.hashCode()).isEqualTo(0x598df91c + "foo".hashCode());
  }

  @Test
  public void absent() {
    assertThat(absent.isPresent()).isFalse();

    assertThat(absent.orNull()).isNull();

    assertThat(absent.or("bar")).isSameAs("bar");

    assertThat(absent.toString()).isEqualTo("Optional.absent()");

    assertThat(absent.equals(present)).isFalse();
    assertThat(absent.equals(absent)).isTrue();

    assertThat(absent.hashCode()).isEqualTo(0x598df91c);

    IllegalStateException thrown = assertThrows(IllegalStateException.class,
      absent::get);
    assertEquals("value is absent", thrown.getMessage());
  }

  @Test
  public void present_or_null() {
    NullPointerException thrown = assertThrows(NullPointerException.class,
      () -> present.or(null));
    assertEquals("use orNull() instead of or(null)", thrown.getMessage());
  }

  @Test
  public void absent_or_null() {
    NullPointerException thrown = assertThrows(NullPointerException.class,
      () -> absent.or(null));
    assertEquals("use orNull() instead of or(null)", thrown.getMessage());
  }

}
