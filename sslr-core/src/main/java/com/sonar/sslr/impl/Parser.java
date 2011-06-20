/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReaderConfiguration;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Preprocessor;
import com.sonar.sslr.api.RecognictionExceptionListener;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer.LexerBuilder;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import com.sonar.sslr.impl.events.Profiler;
import com.sonar.sslr.impl.events.RuleMatcherAdapter;
import com.sonar.sslr.impl.matcher.RuleDefinition;

public class Parser<GRAMMAR extends Grammar> {

  private RuleDefinition rootRule;
  private ParsingState parsingState;
  private LexerOutput lexerOutput;
  private Lexer lexer;
  private GRAMMAR grammar;
  private Set<RecognictionExceptionListener> listeners = new HashSet<RecognictionExceptionListener>();

  private boolean isDecorated = false;
  private boolean enableMemoizer = true;
  private Profiler profiler;
  private ExtendedStackTrace extendedStackTrace;
  private AdaptersDecorator<GRAMMAR> adapterDecorator;

  private Parser(ParserBuilder<GRAMMAR> builder) {
    if (builder.lexer != null) {
      this.lexer = builder.lexer;
    } else {
      this.lexer = builder.lexerBuilder.build();
    }
    this.grammar = builder.grammar;
    if (builder.enableProfiler) this.profiler = new Profiler();
    if (builder.enableExtendedStackTrace) this.extendedStackTrace = new ExtendedStackTrace();
    this.enableMemoizer = builder.enableMemoizer;
    this.listeners = builder.listeners;
    setDecorators(builder.decorators);
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
  }

  protected void setDecorators(List<GrammarDecorator<GRAMMAR>> decorators) {
    for (GrammarDecorator<GRAMMAR> decorator : decorators) {
      decorator.decorate(grammar);
    }
    this.rootRule = (RuleDefinition) grammar.getRootRule();
  }

  public void disableMemoizer() {
    this.enableMemoizer = false;
  }
  
  public void enableProfiler() {
    if (this.profiler == null) this.profiler = new Profiler();
  }

  public void enableExtendedStackTrace() {
    if (this.extendedStackTrace == null) this.extendedStackTrace = new ExtendedStackTrace();
  }

  public void printStackTrace(PrintStream stream) {
    if (this.adapterDecorator == null || this.extendedStackTrace == null) {
      /* Basic stack trace */
      stream.append(ParsingStackTrace.generateFullStackTrace(getParsingState()));
    } else {
      /* Extended stack trace */
      this.extendedStackTrace.printExtendedStackTrace(stream);
    }
  }

  protected void decorate() {
    if (isDecorated)
      return;
    isDecorated = true;
    
    if (this.profiler != null && this.extendedStackTrace != null) {
    	this.adapterDecorator = new AdaptersDecorator<GRAMMAR>(this.enableMemoizer, this.profiler, this.extendedStackTrace);
      this.adapterDecorator.decorate(grammar);
    }
    else if (this.profiler != null) {
    	this.adapterDecorator = new AdaptersDecorator<GRAMMAR>(this.enableMemoizer, this.profiler);
      this.adapterDecorator.decorate(grammar);
    }
    else if (this.extendedStackTrace != null) {
      this.adapterDecorator = new AdaptersDecorator<GRAMMAR>(this.enableMemoizer, this.extendedStackTrace);
      this.adapterDecorator.decorate(grammar);
    } else if (this.enableMemoizer) {
    	this.adapterDecorator = new AdaptersDecorator<GRAMMAR>(this.enableMemoizer);
      this.adapterDecorator.decorate(grammar);
    }
    
  }

  public void addListener(RecognictionExceptionListener listerner) {
    listeners.add(listerner);
  }

  public final AstNode parse(File file) {
    lexerOutput = lexer.lex(file);
    return parse(lexerOutput.getTokens());
  }

  public final AstNode parse(String source) {
    lexerOutput = lexer.lex(source);
    return parse(lexerOutput.getTokens());
  }

  public final AstNode parse(List<Token> tokens) {
    decorate(); /* FIXME: Is there a better place to do this? Perhaps with a Parser Builder! */

    /* (Re)initialize the extended stack trace */
    if (this.extendedStackTrace != null) {
      this.extendedStackTrace.initialize();
    }

    /* Now wrap the root rule (only if required) */
    if (this.extendedStackTrace != null) {
      if ( !(this.rootRule.getRule() instanceof RuleMatcherAdapter)) {
        this.rootRule.setRuleMatcher(new RuleMatcherAdapter(rootRule.getRule(), adapterDecorator.getParsingEventListeners()));
      }
    }

    parsingState = null;
    beforeEachFile();
    try {
      parsingState = new ParsingState(tokens);
      parsingState.setListeners(listeners);
      return rootRule.getRule().match(parsingState);
    } catch (BacktrackingException e) {
      if (parsingState != null) {
        throw new RecognitionException(parsingState);
      } else {
        throw e;
      }
    } catch (StackOverflowError e) {
      throw new RecognitionException("The grammar seems to contain a left recursion which is not compatible with LL(*) parser.",
          parsingState, e);
    } finally {
      GrammarRuleLifeCycleManager.notifyEndParsing(grammar);
      afterEachFile();
    }

  }

  public void beforeEachFile() {
  }

  public void afterEachFile() {
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

  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("Root rule is : " + rootRule.getRule().getName() + "\n");
    result.append("and : " + lexerOutput.toString());
    return result.toString();
  }

  public static <GRAMMAR extends Grammar> ParserBuilder<GRAMMAR> builder(GRAMMAR grammar) {
    return new ParserBuilder<GRAMMAR>(grammar);
  }

  public final static class ParserBuilder<GRAMMAR extends Grammar> {

    private LexerBuilder lexerBuilder = Lexer.builder();
    private Lexer lexer;
    private GRAMMAR grammar;
    private List<GrammarDecorator<GRAMMAR>> decorators = new ArrayList<GrammarDecorator<GRAMMAR>>();
    private boolean enableExtendedStackTrace = false;
    private boolean enableProfiler = false;
    private boolean enableMemoizer = true;
    private Set<RecognictionExceptionListener> listeners = new HashSet<RecognictionExceptionListener>();

    private ParserBuilder(GRAMMAR grammar) {
      this.grammar = grammar;
    }

    public Parser<GRAMMAR> build() {
      return new Parser<GRAMMAR>(this);
    }

    public ParserBuilder<GRAMMAR> optSetCharset(Charset charset) {
      lexerBuilder.optSetCharset(charset);
      return this;
    }

    public ParserBuilder<GRAMMAR> optSetLexer(Lexer lexer) {
      this.lexer = lexer;
      return this;
    }

    public ParserBuilder<GRAMMAR> optAddPreprocessor(Preprocessor preprocessor) {
      lexerBuilder.optAddPreprocessor(preprocessor);
      return this;
    }

    public ParserBuilder<GRAMMAR> optSetCodeReaderConfiguration(CodeReaderConfiguration conf) {
      lexerBuilder.optSetCodeReaderConfiguration(conf);
      return this;
    }

    public ParserBuilder<GRAMMAR> addChannel(Channel<LexerOutput> channel) {
      lexerBuilder.addChannel(channel);
      return this;
    }

    public ParserBuilder<GRAMMAR> optFailIfNoChannelToConsumeOneCharacter() {
      lexerBuilder.optFailIfNoChannelToConsumeOneCharacter();
      return this;
    }

    public ParserBuilder<GRAMMAR> optAddGrammarDecorator(GrammarDecorator<GRAMMAR> decorator) {
      decorators.add(decorator);
      return this;
    }

    public ParserBuilder<GRAMMAR> optEnableExtendedStackTrace() {
      enableExtendedStackTrace = true;
      return this;
    }
    
    public ParserBuilder<GRAMMAR> optEnableProfiler() {
    	enableProfiler = true;
    	return this;
    }

    public ParserBuilder<GRAMMAR> optDisableMemoizer() {
      enableMemoizer = false;
      return this;
    }

    public ParserBuilder<GRAMMAR> optAddRecognictionExceptionListener(RecognictionExceptionListener listener) {
      listeners.add(listener);
      return this;
    }
  }
}
