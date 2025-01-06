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
package org.sonar.sslr.internal.vm;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ErrorLocatingHandlerTest {

  private ErrorLocatingHandler errorLocatingHandler = new ErrorLocatingHandler();

  @Test
  public void should_find_location_of_error() {
    Machine machine = mock(Machine.class);
    when(machine.getIndex()).thenReturn(1);
    errorLocatingHandler.onBacktrack(machine);
    assertThat(errorLocatingHandler.getErrorIndex()).isEqualTo(1);
    when(machine.getIndex()).thenReturn(3);
    errorLocatingHandler.onBacktrack(machine);
    assertThat(errorLocatingHandler.getErrorIndex()).isEqualTo(3);
    when(machine.getIndex()).thenReturn(2);
    errorLocatingHandler.onBacktrack(machine);
    assertThat(errorLocatingHandler.getErrorIndex()).isEqualTo(3);
  }

}
