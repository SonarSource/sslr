/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.toolkit;

import com.google.common.base.Preconditions;

public class ConfigurationProperty {

  private static final ValidationCallback NO_VALIDATION = new ValidationCallback() {
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

  public ConfigurationProperty(String name, String description, String defaultValue, ValidationCallback validationCallback) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(description);
    Preconditions.checkNotNull(defaultValue);
    Preconditions.checkNotNull(validationCallback);

    this.name = name;
    this.description = description;
    this.validationCallback = validationCallback;
    setValue(defaultValue);
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
    Preconditions.checkArgument("".equals(errorMessage), "The value \"" + value + "\" did not pass validation: " + errorMessage);

    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
