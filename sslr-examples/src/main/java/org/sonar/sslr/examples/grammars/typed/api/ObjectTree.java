/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
package org.sonar.sslr.examples.grammars.typed.api;

import javax.annotation.Nullable;
import org.sonar.sslr.examples.grammars.typed.impl.SyntaxList;

public interface ObjectTree extends ValueTree {

  SyntaxToken openCurlyBraceToken();

  @Nullable
  SyntaxList<PairTree> pairs();

  SyntaxToken closeCurlyBraceToken();
}
