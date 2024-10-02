/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.sslr.examples.grammars.typed.impl;

import org.sonar.sslr.examples.grammars.typed.api.LiteralTree;
import org.sonar.sslr.examples.grammars.typed.api.PairTree;
import org.sonar.sslr.examples.grammars.typed.api.SyntaxToken;
import org.sonar.sslr.examples.grammars.typed.api.ValueTree;

public class PairTreeImpl implements PairTree {

  private LiteralTree name;
  private SyntaxToken colonToken;
  private ValueTree value;

  public PairTreeImpl(LiteralTree name, SyntaxToken colonToken, ValueTree value) {
    this.name = name;
    this.colonToken = colonToken;
    this.value = value;
  }

  @Override
  public LiteralTree name() {
    return name;
  }

  @Override
  public SyntaxToken colonToken() {
    return colonToken;
  }

  @Override
  public ValueTree value() {
    return value;
  }
}
