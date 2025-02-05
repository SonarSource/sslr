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

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Loosely modeled after {@link java.util.Optional}.
 *
 * @since 1.21
 */
public abstract class Optional<T> {

  @SuppressWarnings("unchecked")
  public static <T> Optional<T> absent() {
    return (Optional<T>) Absent.INSTANCE;
  }

  public static <T> Optional<T> of(T reference) {
    return new Present<>(Objects.requireNonNull(reference));
  }

  public abstract boolean isPresent();

  public abstract T get();

  public abstract T or(T defaultValue);

  @CheckForNull
  public abstract T orNull();

  private static class Present<T> extends Optional<T> {
    private final T reference;

    public Present(T reference) {
      this.reference = reference;
    }

    @Override
    public boolean isPresent() {
      return true;
    }

    @Override
    public T get() {
      return reference;
    }

    @Override
    public T or(Object defaultValue) {
      Objects.requireNonNull(defaultValue, "use orNull() instead of or(null)");
      return reference;
    }

    @CheckForNull
    @Override
    public T orNull() {
      return reference;
    }

    @Override
    public boolean equals(@Nullable Object object) {
      if (object instanceof Present) {
        Present other = (Present) object;
        return reference.equals(other.reference);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return 0x598df91c + reference.hashCode();
    }

    @Override
    public String toString() {
      return "Optional.of(" + reference + ")";
    }
  }

  private static class Absent extends Optional<Object> {
    private static final Absent INSTANCE = new Absent();

    @Override
    public boolean isPresent() {
      return false;
    }

    @Override
    public Object get() {
      throw new IllegalStateException("value is absent");
    }

    @Override
    public Object or(Object defaultValue) {
      return Objects.requireNonNull(defaultValue, "use orNull() instead of or(null)");
    }

    @CheckForNull
    @Override
    public Object orNull() {
      return null;
    }

    @Override
    public boolean equals(@Nullable Object object) {
      return object == this;
    }

    @Override
    public int hashCode() {
      return 0x598df91c;
    }

    @Override
    public String toString() {
      return "Optional.absent()";
    }
  }

}
