/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

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
