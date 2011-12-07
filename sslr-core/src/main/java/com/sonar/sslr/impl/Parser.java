/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReaderConfiguration;

import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.Lexer.LexerBuilder;
import com.sonar.sslr.impl.events.ParsingEventListener;
import com.sonar.sslr.impl.matcher.GrammarFunctions;
import com.sonar.sslr.impl.matcher.RuleDefinition;

public class Parser<GRAMMAR extends Grammar> {

  private RuleDefinition rootRule;
  private ParsingState parsingState;
  private LexerOutput lexerOutput;
  private Lexer lexer;
  private GRAMMAR grammar;
  private Set<RecognictionExceptionListener> listeners = new HashSet<RecognictionExceptionListener>();
  private ParsingEventListener[] parsingEventListeners;

  private Parser(ParserBuilder<GRAMMAR> builder) {
    if (builder.lexer != null) {
      this.lexer = builder.lexer;
    } else {
      this.lexer = builder.lexerBuilder.build();
    }
    this.grammar = builder.grammar;
    this.parsingEventListeners = builder.parsingEventListeners;
    this.listeners = builder.listeners;
    setDecorators(builder.decorators);
    GrammarFunctions.resetCache();
  }

  /**
   * @deprecated
   * 
   * @see #builder();
   */
  @Deprecated
  public Parser(GRAMMAR grammar, Lexer lexer, GrammarDecorator<GRAMMAR>... decorators) {
    this(grammar, lexer, Arrays.asList(decorators));
  }

  /**
   * @deprecated
   * 
   * @see #builder();
   */
  @Deprecated
  public Parser(GRAMMAR grammar, Lexer lexer, List<GrammarDecorator<GRAMMAR>> decorators) {
    this.grammar = grammar;
    this.lexer = lexer;
    setDecorators(decorators);
    GrammarFunctions.resetCache();
  }

  protected void setDecorators(List<GrammarDecorator<GRAMMAR>> decorators) {
    for (GrammarDecorator<GRAMMAR> decorator : decorators) {
      decorator.decorate(grammar);
    }
    this.rootRule = (RuleDefinition) grammar.getRootRule();
  }

  public void printStackTrace(PrintStream stream) {
    stream.append(ParsingStackTrace.generateFullStackTrace(getParsingState()));
  }

  public void addListener(RecognictionExceptionListener listerner) {
    listeners.add(listerner);
  }

  public final AstNode parse(File file) {
    fireBeginLexEvent();
    lexerOutput = lexer.lex(file);
    fireEndLexEvent();
    return parse(lexerOutput.getTokens());
  }

  public final AstNode parse(String source) {
    fireBeginLexEvent();
    lexerOutput = lexer.lex(source);
    fireEndLexEvent();
    return parse(lexerOutput.getTokens());
  }

  public final AstNode parse(List<Token> tokens) {
    fireBeginParseEvent();

    try {
      parsingState = new ParsingState(tokens);
      parsingState.setListeners(listeners);
      parsingState.parsingEventListeners = parsingEventListeners;
      rootRule.getRule().reinitializeMatcherTree();
      return rootRule.getRule().match(parsingState);
    } catch (BacktrackingEvent e) {
      if (parsingState != null) {
        throw new RecognitionException(parsingState);
      } else {
        throw e;
      }
    } catch (StackOverflowError e) {
      throw new RecognitionException("The grammar seems to contain a left recursion which is not compatible with LL(*) parser.",
          parsingState, e);
    } finally {
      fireEndParseEvent();
    }
  }

  private final void fireBeginLexEvent() {
    if (parsingEventListeners != null) {
      for (ParsingEventListener listener : this.parsingEventListeners) {
        listener.beginLex();
      }
    }
  }

  private final void fireEndLexEvent() {
    if (parsingEventListeners != null) {
      for (ParsingEventListener listener : this.parsingEventListeners) {
        listener.endLex();
      }
    }
  }

  private final void fireEndParseEvent() {
    if (parsingEventListeners != null) {
      for (ParsingEventListener listener : this.parsingEventListeners) {
        listener.endParse();
      }
    }
  }

  private final void fireBeginParseEvent() {
    if (parsingEventListeners != null) {
      for (ParsingEventListener listener : this.parsingEventListeners) {
        listener.beginParse();
      }
    }
  }

  public final ParsingState getParsingState() {
    return parsingState;
  }

  public final GRAMMAR getGrammar() {
    return grammar;
  }

  public final LexerOutput getLexerOutput() {
    return lexerOutput;
  }

  public final RuleDefinition getRootRule() {
    return rootRule;
  }

  public final void setRootRule(Rule rootRule) {
    this.rootRule = (RuleDefinition) rootRule;
  }

  public static <GRAMMAR extends Grammar> ParserBuilder<GRAMMAR> builder(GRAMMAR grammar) {
    return new ParserBuilder<GRAMMAR>(grammar);
  }

  public final static class ParserBuilder<GRAMMAR extends Grammar> {

    private final LexerBuilder lexerBuilder = Lexer.builder();
    private Lexer lexer;
    private final GRAMMAR grammar;
    private final List<GrammarDecorator<GRAMMAR>> decorators = new ArrayList<GrammarDecorator<GRAMMAR>>();
    private ParsingEventListener[] parsingEventListeners;
    private final Set<RecognictionExceptionListener> listeners = new HashSet<RecognictionExceptionListener>();

    private ParserBuilder(GRAMMAR grammar) {
      this.grammar = grammar;
    }

    public Parser<GRAMMAR> build() {
      return new Parser<GRAMMAR>(this);
    }

    public ParserBuilder<GRAMMAR> withCharset(Charset charset) {
      lexerBuilder.withCharset(charset);
      return this;
    }

    public ParserBuilder<GRAMMAR> withLexer(Lexer lexer) {
      this.lexer = lexer;
      return this;
    }

    public ParserBuilder<GRAMMAR> withPreprocessor(Preprocessor preprocessor) {
      lexerBuilder.withPreprocessor(preprocessor);
      return this;
    }

    public ParserBuilder<GRAMMAR> withCodeReaderConfiguration(CodeReaderConfiguration conf) {
      lexerBuilder.withCodeReaderConfiguration(conf);
      return this;
    }

    public ParserBuilder<GRAMMAR> withChannel(Channel<LexerOutput> channel) {
      lexerBuilder.withChannel(channel);
      return this;
    }

    public ParserBuilder<GRAMMAR> withFailIfNoChannelToConsumeOneCharacter(boolean failIfNoChannelToConsumeOneCharacter) {
      lexerBuilder.withFailIfNoChannelToConsumeOneCharacter(failIfNoChannelToConsumeOneCharacter);
      return this;
    }

    public ParserBuilder<GRAMMAR> withGrammarDecorator(GrammarDecorator<GRAMMAR> decorator) {
      decorators.add(decorator);
      return this;
    }

    public ParserBuilder<GRAMMAR> withParsingEventListeners(ParsingEventListener... parsingEventListeners) {
      this.parsingEventListeners = parsingEventListeners;
      return this;
    }

    public ParserBuilder<GRAMMAR> withRecognictionExceptionListener(RecognictionExceptionListener listener) {
      listeners.add(listener);
      return this;
    }

  }

}
