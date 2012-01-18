/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.*;

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
  public void startLexing() {
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

  private void preprocessBuffer(LexerOutput output) {
    /* Here is where we should interpret the tokens, but there is no need in this case */

    /* Push the preprocessed trivia */
    output.addTrivia(Trivia.createPreprocessorTrivia(structure, structureGrammar));
  }

  @Override
  public boolean process(Token token, LexerOutput output) {
    if ( !buffer.isEmpty() && (token.getType() == EOF || token.getLine() != buffer.get(0).getLine())) {
      if (isBufferValid()) {
        preprocessBuffer(output);
      } else {
        output.pushBackTokensAndProcess(buffer, this);
      }
      buffer.clear();
    }

    if (buffer.isEmpty()) {
      if (token.getType() == HASH) {
        buffer.add(token);
        return true;
      }

      return false;
    } else {
      buffer.add(token);
      return true;
    }
  }

}
