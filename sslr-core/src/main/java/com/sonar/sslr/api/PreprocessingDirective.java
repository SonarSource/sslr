/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2018 SonarSource SA
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
package com.sonar.sslr.api;

/**
 * @deprecated in 1.20, use your own preprocessor API instead.
 */
@Deprecated
public abstract class PreprocessingDirective {

  public abstract AstNode getAst();

  public abstract Grammar getGrammar();

  public static PreprocessingDirective create(AstNode ast, Grammar grammar) {
    return new DefaultPreprocessingDirective(ast, grammar);
  }

  private static final class DefaultPreprocessingDirective extends PreprocessingDirective {

    private final AstNode astNode;
    private final Grammar grammar;

    private DefaultPreprocessingDirective(AstNode ast, Grammar grammar) {
      this.astNode = ast;
      this.grammar = grammar;
    }

    @Override
    public AstNode getAst() {
      return astNode;
    }

    @Override
    public Grammar getGrammar() {
      return grammar;
    }

  }

}
