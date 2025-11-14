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
package org.sonar.sslr.grammar;

/**
 * Indicates problem with definition of grammar.
 * If you face with this exception, then you should fix code, which defines your grammar.
 *
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 *
 * @since 1.18
 */
public class GrammarException extends RuntimeException {

  public GrammarException(String message) {
    super(message);
  }

  public GrammarException(Throwable cause, String message) {
    super(message, cause);
  }

}
