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

import java.util.Objects;

/**
 * This class represents a configuration property, which is made of a name, a description (which may be empty),
 * a default value, and optionnally a validation callback.
 *
 * @since 1.17
 */
public class ConfigurationProperty {

  private static final ValidationCallback NO_VALIDATION = new ValidationCallback() {
    @Override
    public String validate(String newValueCandidate) {
      return "";
    }
  };

  private final String name;
  private final String description;
  private String value;
  private final ValidationCallback validationCallback;

  public ConfigurationProperty(String name, String description, String defaultValue) {
    this(name, description, defaultValue, NO_VALIDATION);
  }

  /**
   *
   * @param name
   * @param description
   * @param defaultValue
   * @param validationCallback The validation callback. Note that handy ones are available out-of-the-box by the {@link Validators} class.
   */
  public ConfigurationProperty(String name, String description, String defaultValue, ValidationCallback validationCallback) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(description);
    Objects.requireNonNull(defaultValue);
    Objects.requireNonNull(validationCallback);

    String errorMessage = validationCallback.validate(defaultValue);
    if (!"".equals(errorMessage)) {
      throw new IllegalArgumentException("The default value \"" + defaultValue + "\" did not pass validation: " + errorMessage);
    }

    this.name = name;
    this.description = description;
    this.validationCallback = validationCallback;
    this.value = defaultValue;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String validate(String newValueCandidate) {
    return validationCallback.validate(newValueCandidate);
  }

  public void setValue(String value) {
    String errorMessage = validate(value);
    if (!"".equals(errorMessage)) {
      throw new IllegalArgumentException("The value \"" + value + "\" did not pass validation: " + errorMessage);
    }

    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
