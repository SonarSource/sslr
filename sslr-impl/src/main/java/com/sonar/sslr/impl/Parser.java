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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.RecognictionExceptionListener;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.events.EventAdapterDecorator;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import com.sonar.sslr.impl.events.RuleMatcherAdapter;
import com.sonar.sslr.impl.matcher.RuleBuilder;

public abstract class Parser<GRAMMAR extends Grammar> {

  private RuleBuilder rootRule;
  private ParsingState parsingState;
  private LexerOutput lexerOutput;
  private Lexer lexer;
  private GRAMMAR grammar;
  private Set<RecognictionExceptionListener> listeners = new HashSet<RecognictionExceptionListener>();

  private boolean isDecorated = false;
  private boolean enableMemoizer = true;
  private boolean enableExtendedStackTrace = false;
  private EventAdapterDecorator<GRAMMAR> eventAdapterDecorator;

  public Parser(GRAMMAR grammar, Lexer lexer, GrammarDecorator<GRAMMAR>... decorators) {
    this(grammar, lexer, Arrays.asList(decorators));
  }

  public Parser(GRAMMAR grammar, Lexer lexer, List<GrammarDecorator<GRAMMAR>> decorators) {
    this.grammar = grammar;
    this.lexer = lexer;
    setDecorators(decorators);
  }

  protected void setDecorators(List<GrammarDecorator<GRAMMAR>> decorators) {
    for (GrammarDecorator<GRAMMAR> decorator : decorators) {
      decorator.decorate(grammar);
    }
    this.rootRule = (RuleBuilder) grammar.getRootRule();
  }

  public void disableMemoizer() {
    this.enableMemoizer = false;
  }

  public void enableExtendedStackTrace() {
    this.enableExtendedStackTrace = true;
  }

  public void printStackTrace(PrintStream stream) {
    if (this.eventAdapterDecorator == null || !(this.eventAdapterDecorator.getParsingEventListener() instanceof ExtendedStackTrace)) {
      /* Basic stack trace */
      stream.append(ParsingStackTrace.generateFullStackTrace(getParsingState()));
    } else {
      /* Extended stack trace */
      ((ExtendedStackTrace) this.eventAdapterDecorator.getParsingEventListener()).printExtendedStackTrace(stream);
    }
  }

  protected void decorate() {
    if (isDecorated)
      return;
    isDecorated = true;

    if (enableMemoizer)
      new MemoizerAdapterDecorator<GRAMMAR>().decorate(grammar);
    if (enableExtendedStackTrace) {
      this.eventAdapterDecorator = new EventAdapterDecorator<GRAMMAR>(new ExtendedStackTrace());
      this.eventAdapterDecorator.decorate(grammar);
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
    if (enableExtendedStackTrace) {
      ((ExtendedStackTrace) this.eventAdapterDecorator.getParsingEventListener()).initialize();
    }

    /* Now wrap the root rule (only if required) */
    if (enableExtendedStackTrace) {
      if ( !(this.rootRule.getRule() instanceof RuleMatcherAdapter)) {
        this.rootRule.replaceRuleMatcher(new RuleMatcherAdapter(eventAdapterDecorator.getParsingEventListener(), rootRule.getRule()));
      }
    }

    parsingState = null;
    beforeEachFile();
    try {
      parsingState = new ParsingState(tokens);
      parsingState.setListeners(listeners);
      return rootRule.getRule().match(parsingState);
    } catch (RecognitionExceptionImpl e) {
      if (parsingState != null) {
        throw new RecognitionExceptionImpl(parsingState);
      } else {
        throw e;
      }
    } catch (StackOverflowError e) {
      throw new RecognitionExceptionImpl("The grammar seems to contain a left recursion which is not compatible with LL(*) parser.",
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

  public final RuleBuilder getRootRule() {
    return rootRule;
  }

  public final void setRootRule(Rule rootRule) {
    this.rootRule = (RuleBuilder) rootRule;
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("Root rule is : " + rootRule.getRule().getName() + "\n");
    result.append("and : " + lexerOutput.toString());
    return result.toString();
  }
}
