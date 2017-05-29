/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2017 SonarSource SA
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
package com.sonar.sslr.impl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.sonar.sslr.internal.matchers.LexerfulAstCreator;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;
import org.sonar.sslr.internal.vm.CompiledGrammar;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.MutableGrammarCompiler;
import org.sonar.sslr.parser.ParserAdapter;

import java.io.File;
import java.util.List;

/**
 * To create a new instance of this class use <code>{@link Parser#builder(Grammar)}</code>.
 *
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 */
public class Parser<G extends Grammar> {

  private RuleDefinition rootRule;
  private final Lexer lexer;
  private final G grammar;

  /**
   * @since 1.16
   */
  protected Parser(G grammar) {
    this.grammar = grammar;
    lexer = null;
  }

  private Parser(Builder<G> builder) {
    this.lexer = builder.lexer;
    this.grammar = builder.grammar;
    this.rootRule = (RuleDefinition) this.grammar.getRootRule();
  }

  public AstNode parse(File file) {
    try {
      lexer.lex(file);
    } catch (LexerException e) {
      throw new RecognitionException(e);
    }
    return parse(lexer.getTokens());
  }

  public AstNode parse(String source) {
    try {
      lexer.lex(source);
    } catch (LexerException e) {
      throw new RecognitionException(e);
    }
    return parse(lexer.getTokens());
  }

  public AstNode parse(List<Token> tokens) {
    // TODO can be compiled only once
    CompiledGrammar g = MutableGrammarCompiler.compile((CompilableGrammarRule) rootRule);
    return LexerfulAstCreator.create(Machine.parse(tokens, g), tokens);
  }

  public G getGrammar() {
    return grammar;
  }

  public RuleDefinition getRootRule() {
    return rootRule;
  }

  public void setRootRule(Rule rootRule) {
    this.rootRule = (RuleDefinition) rootRule;
  }

  public static <G extends Grammar> Builder<G> builder(G grammar) {
    return new Builder<>(grammar);
  }

  public static <G extends Grammar> Builder<G> builder(Parser<G> parser) {
    return new Builder<>(parser);
  }

  public static final class Builder<G extends Grammar> {

    private Parser<G> baseParser;
    private Lexer lexer;
    private final G grammar;

    private Builder(G grammar) {
      this.grammar = grammar;
    }

    private Builder(Parser<G> parser) {
      this.baseParser = parser;
      this.lexer = parser.lexer;
      this.grammar = parser.grammar;
    }

    public Parser<G> build() {
      if (baseParser instanceof ParserAdapter) {
        return baseParser;
      }
      return new Parser<>(this);
    }

    public Builder<G> withLexer(Lexer lexer) {
      this.lexer = lexer;
      return this;
    }

  }

}
