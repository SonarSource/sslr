/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import java.io.File;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleImpl;

public abstract class Parser {

  private RuleImpl rootRule;
  private ParsingState parsingState;
  private LexerOutput lexerOutput;
  private Lexer lexer;
  private Grammar grammar;

  public Parser(Grammar grammar, Lexer lexer) {
    this.grammar = grammar;
    rootRule = (RuleImpl) grammar.getRootRule();
    this.lexer = lexer;
  }

  public Parser(Grammar grammar, Rule rootRule, Lexer lexer) {
    this(grammar, lexer);
    this.rootRule = (RuleImpl) rootRule;
  }

  public AstNode parse(File file) {
    try {
      parsingState = null;
      lexerOutput = lexer.lex(file);
      parsingState = new ParsingState(lexerOutput);
      return rootRule.match(parsingState);
    } catch (RecognitionExceptionImpl e) {
      if (parsingState != null) {
        throw new RecognitionExceptionImpl(parsingState);
      } else {
        throw e;
      }
    }
  }

  public AstNode parse(String source) {
    try {
      lexerOutput = lexer.lex(source);
      parsingState = new ParsingState(lexerOutput);
      return rootRule.match(parsingState);
    } catch (RecognitionExceptionImpl e) {
      throw new RecognitionExceptionImpl(parsingState);
    }
  }

  public final ParsingState getParsingState() {
    return parsingState;
  }

  public final Grammar getGrammar() {
    return grammar;
  }

  public final LexerOutput getLexerOutput() {
    return lexerOutput;
  }
}
