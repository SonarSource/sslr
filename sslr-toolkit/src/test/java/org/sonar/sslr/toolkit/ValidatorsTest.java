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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.Assertions.assertThat;

public class ValidatorsTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void charsetValidator() {
    ValidationCallback validator = Validators.charsetValidator();
    assertThat(validator.validate("UTF-8")).isEmpty();
    assertThat(validator.validate("ISO-8859-15")).isEmpty();
    assertThat(validator.validate("foo")).isEqualTo("Unsupported charset: foo");
    assertThat(validator.validate(" ")).isEqualTo("Illegal charset:  ");
  }

  @Test
  public void charsetValidator_single_instance() {
    assertThat(Validators.charsetValidator()).isSameAs(Validators.charsetValidator());
  }

  @Test
  public void integerRangeValidator() {
    ValidationCallback validator = Validators.integerRangeValidator(0, 42);
    assertThat(validator.validate("24")).isEmpty();
    assertThat(validator.validate("-100")).isEqualTo("Must be between 0 and 42: -100");
    assertThat(validator.validate("100")).isEqualTo("Must be between 0 and 42: 100");
    assertThat(validator.validate("foo")).isEqualTo("Not an integer: foo");

    assertThat(Validators.integerRangeValidator(42, 42).validate("43")).isEqualTo("Must be equal to 42: 43");
    assertThat(Validators.integerRangeValidator(Integer.MIN_VALUE, 0).validate("1")).isEqualTo("Must be negative or 0: 1");
    assertThat(Validators.integerRangeValidator(Integer.MIN_VALUE, -1).validate("0")).isEqualTo("Must be strictly negative: 0");
    assertThat(Validators.integerRangeValidator(Integer.MIN_VALUE, 42).validate("43")).isEqualTo("Must be lower or equal to 42: 43");
    assertThat(Validators.integerRangeValidator(0, Integer.MAX_VALUE).validate("-1")).isEqualTo("Must be positive or 0: -1");
    assertThat(Validators.integerRangeValidator(1, Integer.MAX_VALUE).validate("0")).isEqualTo("Must be strictly positive: 0");
    assertThat(Validators.integerRangeValidator(42, Integer.MAX_VALUE).validate("41")).isEqualTo("Must be greater or equal to 42: 41");
  }

  @Test
  public void integerRangeValidator_should_fail_with_upper_smaller_than_lower_bound() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("lowerBound(42) <= upperBound(0)");

    Validators.integerRangeValidator(42, 0);
  }

  @Test
  public void booleanValidator() {
    ValidationCallback validator = Validators.booleanValidator();
    assertThat(validator.validate("true")).isEmpty();
    assertThat(validator.validate("false")).isEmpty();
    assertThat(validator.validate("foo")).isEqualTo("Must be either \"true\" or \"false\": foo");
  }

  @Test
  public void booleanValidator_single_instance() {
    assertThat(Validators.booleanValidator()).isSameAs(Validators.booleanValidator());
  }

}
