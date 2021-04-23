/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
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

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ConfigurationPropertyTest {

  @Test
  public void getName() {
    assertThat(new ConfigurationProperty("foo", "", "").getName()).isEqualTo("foo");
    assertThat(new ConfigurationProperty("bar", "", "").getName()).isEqualTo("bar");
  }

  @Test
  public void getDescription() {
    assertThat(new ConfigurationProperty("", "foo", "").getDescription()).isEqualTo("foo");
    assertThat(new ConfigurationProperty("", "bar", "").getDescription()).isEqualTo("bar");
  }

  @Test
  public void validate() {
    assertThat(new ConfigurationProperty("", "", "").validate("")).isEmpty();
    assertThat(new ConfigurationProperty("", "", "").validate("foo")).isEmpty();

    ConfigurationProperty property = new ConfigurationProperty("", "", "foo", new ValidationCallback() {
      @Override
      public String validate(String newValueCandidate) {
        return "foo".equals(newValueCandidate) ? "" : "Only the value \"foo\" is allowed.";
      }
    });
    assertThat(property.validate("")).isEqualTo("Only the value \"foo\" is allowed.");
    assertThat(property.validate("foo")).isEmpty();
    assertThat(property.validate("bar")).isEqualTo("Only the value \"foo\" is allowed.");
  }

  @Test
  public void setValue_should_succeed_if_validation_passes() {
    new ConfigurationProperty("", "", "").setValue("");
    new ConfigurationProperty("", "", "").setValue("foo");
  }

  @Test
  public void setValue_should_fail_if_validation_fails() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      () -> new ConfigurationProperty("", "", "", new ValidationCallback() {
        @Override
        public String validate(String newValueCandidate) {
          return newValueCandidate.isEmpty() ? "" : "The value \"" + newValueCandidate + "\" did not pass validation: Not valid!";
        }
      }).setValue("foo"));
    assertTrue(thrown.getMessage().contains("The value \"foo\" did not pass validation: Not valid!"));
  }

  @Test
  public void getValue() {
    assertThat(new ConfigurationProperty("", "", "").getValue()).isEqualTo("");
    assertThat(new ConfigurationProperty("", "", "foo").getValue()).isEqualTo("foo");

    ConfigurationProperty property = new ConfigurationProperty("", "", "");
    assertThat(property.getValue()).isEqualTo("");
    property.setValue("foo");
    assertThat(property.getValue()).isEqualTo("foo");
  }

}
