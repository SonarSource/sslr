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
package org.sonar.sslr.tests;

import org.junit.ComparisonFailure;

/**
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 *
 * @since 1.16
 */
public class ParsingResultComparisonFailure extends ComparisonFailure {

  private final String message;

  public ParsingResultComparisonFailure(String expected, String actual) {
    this(expected + '\n' + actual, expected, actual);
  }

  public ParsingResultComparisonFailure(String message, String expected, String actual) {
    super(message, expected, actual);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

}
