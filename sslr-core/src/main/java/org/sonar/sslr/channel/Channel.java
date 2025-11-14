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
package org.sonar.sslr.channel;

public abstract class Channel<O> {

  /**
   * Tries to consume the character stream at the current reading cursor position (provided by the {@link org.sonar.sslr.channel.CodeReader}). If
   * the character stream is consumed the method must return true and the OUTPUT object can be fed.
   * 
   * @param code
   *          the handle on the input character stream
   * @param output
   *          the OUTPUT that can be optionally fed by the Channel
   * @return false if the Channel doesn't want to consume the character stream, true otherwise.
   */
  public abstract boolean consume(CodeReader code, O output);
}
