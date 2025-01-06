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
package org.sonar.sslr.toolkit;

/**
 * This interface is used to validate values that are to be assigned to configuration properties.
 *
 * The {@link Validators} class provides out-of-the-box handy validators.
 *
 * @since 1.17
 */
public interface ValidationCallback {

  /**
   * Validate the new value candidate.
   *
   * @param newValueCandidate The value to be validated
   * @return The empty string if validation passed, else a non-empty error message
   */
  String validate(String newValueCandidate);

}
