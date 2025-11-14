/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;

/**
 * @since 1.16
 */
public final class Assertions {

  private Assertions() {
  }

  /**
   * Creates a new instance of <code>{@link RuleAssert}</code>.
   * @param actual the value to be the target of the assertions methods.
   * @return the created assertion object.
   */
  public static RuleAssert assertThat(Rule actual) {
    return new RuleAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link ParserAssert}</code>.
   * @param actual the value to be the target of the assertions methods.
   * @return the created assertion object.
   */
  public static ParserAssert assertThat(Parser actual) {
    return new ParserAssert(actual);
  }

}
