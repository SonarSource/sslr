/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.*;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.Parser;

public class MiniCPreprocessor extends Preprocessor {

  private final List<Token> buffer = Lists.newLinkedList();
  private AstNode structure;
  private Grammar structureGrammar;

  public class MiniCPreprocessorGrammar extends Grammar {

    public Rule defineDirective;
    public Rule binDefinition;

    public MiniCPreprocessorGrammar() {
      defineDirective.is(HASH, "define", binDefinition);
      binDefinition.is(IDENTIFIER);
    }

    @Override
    public Rule getRootRule() {
      return defineDirective;
    }
  }

  @Override
  public void init() {
    buffer.clear();
  }

  private boolean isBufferValid() {
    /* Launch the preprocessor parser on buffer, and set the resulting AstNode on the first token */
    Parser<MiniCPreprocessorGrammar> parser = Parser.builder(new MiniCPreprocessorGrammar()).build();
    try {
      AstNode node = parser.parse(buffer);
      this.structure = node;
      this.structureGrammar = parser.getGrammar();
      return true;
    } catch (RecognitionException re) {
      return false;
    }
  }

  private Trivia preprocessBuffer() {
    /* Here is where we should interpret the tokens, but there is no need in this case */

    /* Push the preprocessed trivia */
    return Trivia.createPreprocessingDirective(structure, structureGrammar);
  }

  @Override
  public PreprocessorAction process(List<Token> tokens) {
    List<Trivia> triviaToInject = new LinkedList<Trivia>();

    if ( !buffer.isEmpty() && (tokens.get(0).getType() == EOF || tokens.get(0).getLine() != buffer.get(0).getLine())) {
      if (isBufferValid()) {
        triviaToInject.add(preprocessBuffer());
      } else {
        throw new IllegalStateException("FIXME Pushback would be required!"); /* FIXME */
      }
      buffer.clear();
    }

    if (buffer.isEmpty()) {
      if (tokens.get(0).getType() == HASH) {
        buffer.add(tokens.get(0));
        return new PreprocessorAction(1, triviaToInject, new LinkedList<Token>());
      }

      return new PreprocessorAction(0, triviaToInject, new LinkedList<Token>());
    } else {
      buffer.add(tokens.get(0));
      return new PreprocessorAction(1, triviaToInject, new LinkedList<Token>());
    }
  }

}
