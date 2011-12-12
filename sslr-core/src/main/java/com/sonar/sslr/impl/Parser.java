/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import com.sonar.sslr.impl.events.ParsingEventListener;
import com.sonar.sslr.impl.matcher.GrammarFunctions;
import com.sonar.sslr.impl.matcher.RuleDefinition;

public class Parser<GRAMMAR extends Grammar> {

  private RuleDefinition rootRule;
  private ParsingState parsingState;
  private LexerOutput lexerOutput;
  private Lexer lexer;
  private GRAMMAR grammar;
  private Set<RecognitionExceptionListener> listeners = new HashSet<RecognitionExceptionListener>();
  private ParsingEventListener[] parsingEventListeners;
  private ExtendedStackTrace extendedStackTrace;

  private Parser(ParserBuilder<GRAMMAR> builder) {
    this.lexer = builder.lexer;
    this.grammar = builder.grammar;
    this.listeners = builder.listeners;

    this.extendedStackTrace = builder.extendedStackTrace;
    if (this.extendedStackTrace != null) {
      this.parsingEventListeners = builder.parsingEventListeners
          .toArray(new ParsingEventListener[builder.parsingEventListeners.size() + 1]);
      this.parsingEventListeners[this.parsingEventListeners.length - 1] = this.extendedStackTrace;
    } else {
      this.parsingEventListeners = builder.parsingEventListeners.toArray(new ParsingEventListener[builder.parsingEventListeners.size()]);
    }

    GrammarFunctions.resetCache();
    this.rootRule = (RuleDefinition) this.grammar.getRootRule();
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

  public void addListener(RecognitionExceptionListener listerner) {
    listeners.add(listerner);
  }

  public final AstNode parse(File file) {
    fireBeginLexEvent();
    try {
      lexerOutput = lexer.lex(file);
    } catch (LexerException e) {
      throw new RecognitionException(e);
    } finally {
      fireEndLexEvent();
    }
    return parse(lexerOutput.getTokens());
  }

  public final AstNode parse(String source) {
    fireBeginLexEvent();
    try {
      lexerOutput = lexer.lex(source);
    } catch (LexerException e) {
      throw new RecognitionException(e);
    } finally {
      fireEndLexEvent();
    }
    return parse(lexerOutput.getTokens());
  }

  public final AstNode parse(List<Token> tokens) {
    fireBeginParseEvent();

    try {
      parsingState = new ParsingState(tokens);
      parsingState.setListeners(listeners);
      parsingState.parsingEventListeners = parsingEventListeners;
      parsingState.extendedStackTrace = extendedStackTrace;
      rootRule.getRule().reinitializeMatcherTree();
      return rootRule.getRule().match(parsingState);
    } catch (BacktrackingEvent e) {
      throw extendedStackTrace == null ? new RecognitionException(parsingState, true) : new RecognitionException(extendedStackTrace, true);
    } catch (StackOverflowError e) {
      throw new RecognitionException("The grammar seems to contain a left recursion which is not compatible with LL(*) parser.",
          parsingState, true, e);
    } finally {
      fireEndParseEvent();
    }
  }

  private void fireBeginLexEvent() {
    if (parsingEventListeners != null) {
      for (ParsingEventListener listener : this.parsingEventListeners) {
        listener.beginLex();
      }
    }
  }

  private void fireEndLexEvent() {
    if (parsingEventListeners != null) {
      for (ParsingEventListener listener : this.parsingEventListeners) {
        listener.endLex();
      }
    }
  }

  private void fireEndParseEvent() {
    if (parsingEventListeners != null) {
      for (ParsingEventListener listener : this.parsingEventListeners) {
        listener.endParse();
      }
    }
  }

  private void fireBeginParseEvent() {
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

  public static <GRAMMAR extends Grammar> ParserBuilder<GRAMMAR> builder(Parser<GRAMMAR> parser) {
    return new ParserBuilder<GRAMMAR>(parser);
  }

  public final static class ParserBuilder<GRAMMAR extends Grammar> {

    private Lexer lexer;
    private final GRAMMAR grammar;
    private final Set<ParsingEventListener> parsingEventListeners = new HashSet<ParsingEventListener>();
    private final Set<RecognitionExceptionListener> listeners = new HashSet<RecognitionExceptionListener>();
    private ExtendedStackTrace extendedStackTrace;

    private ParserBuilder(GRAMMAR grammar) {
      this.grammar = grammar;
    }

    private ParserBuilder(Parser<GRAMMAR> parser) {
      this.lexer = parser.lexer;
      this.grammar = parser.grammar;
      setParsingEventListeners(parser.parsingEventListeners);
      setRecognictionExceptionListener(parser.listeners.toArray(new RecognitionExceptionListener[parser.listeners.size()]));
      this.extendedStackTrace = parser.extendedStackTrace;
    }

    public Parser<GRAMMAR> build() {
      return new Parser<GRAMMAR>(this);
    }

    public ParserBuilder<GRAMMAR> withLexer(Lexer lexer) {
      this.lexer = lexer;
      return this;
    }

    public ParserBuilder<GRAMMAR> setParsingEventListeners(ParsingEventListener... parsingEventListeners) {
      this.parsingEventListeners.clear();
      addParsingEventListeners(parsingEventListeners);
      return this;
    }

    public ParserBuilder<GRAMMAR> addParsingEventListeners(ParsingEventListener... parsingEventListeners) {
      for (ParsingEventListener parsingEventListener : parsingEventListeners) {
        this.parsingEventListeners.add(parsingEventListener);
      }
      return this;
    }

    public ParserBuilder<GRAMMAR> setRecognictionExceptionListener(RecognitionExceptionListener... listeners) {
      this.listeners.clear();
      addRecognictionExceptionListeners(listeners);
      return this;
    }

    public ParserBuilder<GRAMMAR> addRecognictionExceptionListeners(RecognitionExceptionListener... listeners) {
      for (RecognitionExceptionListener listener : listeners) {
        this.listeners.add(listener);
      }
      return this;
    }

    public ParserBuilder<GRAMMAR> setExtendedStackTrace(ExtendedStackTrace extendedStackTrace) {
      this.extendedStackTrace = extendedStackTrace;
      return this;
    }

  }

}
