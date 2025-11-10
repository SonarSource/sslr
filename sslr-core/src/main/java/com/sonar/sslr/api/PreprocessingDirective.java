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
