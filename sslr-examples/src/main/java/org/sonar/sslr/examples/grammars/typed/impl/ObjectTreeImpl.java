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
package org.sonar.sslr.examples.grammars.typed.impl;

import javax.annotation.Nullable;
import org.sonar.sslr.examples.grammars.typed.api.ObjectTree;
import org.sonar.sslr.examples.grammars.typed.api.PairTree;
import org.sonar.sslr.examples.grammars.typed.api.SyntaxToken;

public class ObjectTreeImpl implements ObjectTree {

  private SyntaxToken openCurlyBraceToken;
  private SyntaxList<PairTree> pairs;
  private SyntaxToken closeCurlyBraceToken;

  public ObjectTreeImpl(SyntaxToken openCurlyBraceToken, SyntaxList<PairTree> pairs, SyntaxToken closeCurlyBraceToken) {
    this.openCurlyBraceToken = openCurlyBraceToken;
    this.pairs = pairs;
    this.closeCurlyBraceToken = closeCurlyBraceToken;
  }

  @Override
  public SyntaxToken openCurlyBraceToken() {
    return openCurlyBraceToken;
  }

  @Nullable
  @Override
  public SyntaxList<PairTree> pairs() {
    return pairs;
  }

  @Override
  public SyntaxToken closeCurlyBraceToken() {
    return closeCurlyBraceToken;
  }
}
