/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
