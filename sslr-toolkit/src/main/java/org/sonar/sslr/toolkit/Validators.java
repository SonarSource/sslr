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

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Provides a few handy configuration property validators out-of-the-box.
 *
 * @since 1.17
 */
public class Validators {

  private static final CharsetValidator CHARSET_VALIDATOR = new CharsetValidator();
  private static final BooleanValidator BOOLEAN_VALIDATOR = new BooleanValidator();

  private Validators() {
  }

  /**
   * Validates that the property holds a valid {@link Charset} name.
   *
   * @return A charset validator
   */
  public static ValidationCallback charsetValidator() {
    return CHARSET_VALIDATOR;
  }

  private static class CharsetValidator implements ValidationCallback {

    @Override
    public String validate(String newValueCandidate) {
      try {
        Charset.forName(newValueCandidate);
        return "";
      } catch (IllegalCharsetNameException e) {
        return "Illegal charset: " + e.getMessage();
      } catch (UnsupportedCharsetException e) {
        return "Unsupported charset: " + e.getMessage();
      }
    }

  }

  /**
   * Validates that the property holds an integer within the given lower and upper bounds.
   *
   * @param lowerBound
   * @param upperBound
   * @return An integer range validator
   */
  public static ValidationCallback integerRangeValidator(int lowerBound, int upperBound) {
    return new IntegerRangeValidator(lowerBound, upperBound);
  }

  private static class IntegerRangeValidator implements ValidationCallback {

    private final int lowerBound;
    private final int upperBound;

    public IntegerRangeValidator(int lowerBound, int upperBound) {
      Preconditions.checkArgument(lowerBound <= upperBound, "lowerBound(" + lowerBound + ") <= upperBound(" + upperBound + ")");

      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
    }

    @Override
    public String validate(String newValueCandidate) {
      try {
        int value = Integer.parseInt(newValueCandidate);

        if (value < lowerBound || value > upperBound) {
          return getErrorMessage(value);
        } else {
          return "";
        }
      } catch (NumberFormatException e) {
        return "Not an integer: " + newValueCandidate;
      }
    }

    private String getErrorMessage(int value) {
      String errorMessage;

      if (lowerBound == upperBound) {
        errorMessage = "Must be equal to " + lowerBound + ": " + value;
      } else if (upperBound == Integer.MAX_VALUE) {
        if (lowerBound == 0) {
          errorMessage = "Must be positive or 0: " + value;
        } else if (lowerBound == 1) {
          errorMessage = "Must be strictly positive: " + value;
        } else {
          errorMessage = "Must be greater or equal to " + lowerBound + ": " + value;
        }
      } else if (lowerBound == Integer.MIN_VALUE) {
        if (upperBound == 0) {
          errorMessage = "Must be negative or 0: " + value;
        } else if (upperBound == -1) {
          errorMessage = "Must be strictly negative: " + value;
        } else {
          errorMessage = "Must be lower or equal to " + upperBound + ": " + value;
        }
      } else {
        errorMessage = "Must be between " + lowerBound + " and " + upperBound + ": " + value;
      }

      return errorMessage;
    }
  }

  /**
   * Validates that the property holds a boolean value, i.e. either "true" or "false".
   *
   * @return A boolean validator
   */
  public static ValidationCallback booleanValidator() {
    return BOOLEAN_VALIDATOR;
  }

  private static class BooleanValidator implements ValidationCallback {

    @Override
    public String validate(String newValueCandidate) {
      return !"false".equals(newValueCandidate) && !"true".equals(newValueCandidate) ?
          "Must be either \"true\" or \"false\": " + newValueCandidate :
          "";
    }

  }

}
