/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.longestOne;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.Parser;

public class ExtendedStackTraceTest {

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;
    public Rule rule2;

    public Rule getRootRule() {
      return root;
    }

  }

  private class MyTestGrammarDecoratorV1 implements GrammarDecorator<MyTestGrammar> {

    public void decorate(MyTestGrammar t) {
      t.root.is("bonjour", longestOne(t.rule1, t.rule2), and("olaa", "uhu"), EOF);
      t.rule1.is("hehe");
      t.rule2.is("hehe", "huhu");
    }
  }

  private class MyTestGrammarDecoratorV2 implements GrammarDecorator<MyTestGrammar> {

    public void decorate(MyTestGrammar t) {
      t.root.is("bonjour", longestOne(t.rule1, t.rule2), and("olaa", "uhu"), EOF);
      t.rule1.is("hehe");
      t.rule2.is("hehe", "huhu", "wtf");
    }
  }
  
  private class MyTestGrammarDecoratorV3 implements GrammarDecorator<MyTestGrammar> {

    public void decorate(MyTestGrammar t) {
      t.root.is("bonjour", "hehe", EOF, "next");
    }
  }


  @Test
  public void ok() {
    Parser<MyTestGrammar> p = Parser.builder(new MyTestGrammar()).optSetLexer(IdentifierLexer.create())
        .optAddGrammarDecorator(new MyTestGrammarDecoratorV1()).withMemoizer(false).withExtendedStackTrace(true).build();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      p.parse("bonjour hehe huhu haha");
      throw new IllegalStateException();
    } catch (RecognitionException ex) {
      p.printStackTrace(new PrintStream(baos));
    }

    StringBuilder expected = new StringBuilder();
    expected.append("Source Snippet:" + System.getProperty("line.separator"));
    expected.append("---------------" + System.getProperty("line.separator"));
    expected.append("  --> bonjour hehe huhu hahaEOF" + System.getProperty("line.separator"));
    expected.append("---------------" + System.getProperty("line.separator"));
    expected.append("" + System.getProperty("line.separator"));
    expected.append("on matcher and(\"olaa\", \"uhu\")" + System.getProperty("line.separator"));
    expected.append("       1 :     18  : \"olaa\" expected but \"haha\" [IDENTIFIER] found" + System.getProperty("line.separator"));
    expected.append("at root" + System.getProperty("line.separator"));
    expected.append("       1 :      0  : bonjour hehe huhu " + System.getProperty("line.separator"));
    expected.append("" + System.getProperty("line.separator"));
    expected.append("Last successful tokens:" + System.getProperty("line.separator"));
    expected.append("-----------------------" + System.getProperty("line.separator"));
    expected.append("  \"huhu\" at 1:13 consumed by root" + System.getProperty("line.separator"));
    expected.append("  \"hehe\" at 1:8 consumed by root" + System.getProperty("line.separator"));
    expected.append("  \"bonjour\" at 1:0 consumed by root" + System.getProperty("line.separator"));

    assertEquals(baos.toString(), expected.toString());

    p = Parser.builder(new MyTestGrammar()).optSetLexer(IdentifierLexer.create()).optAddGrammarDecorator(new MyTestGrammarDecoratorV2())
        .withMemoizer(false).withExtendedStackTrace(true).build();

    baos = new ByteArrayOutputStream();

    try {
      p.parse("bonjour hehe huhu haha");
      throw new IllegalStateException();
    } catch (RecognitionException ex) {
      p.printStackTrace(new PrintStream(baos));
    }

    expected = new StringBuilder();
    expected.append("Source Snippet:" + System.getProperty("line.separator"));
    expected.append("---------------" + System.getProperty("line.separator"));
    expected.append("  --> bonjour hehe huhu hahaEOF" + System.getProperty("line.separator"));
    expected.append("---------------" + System.getProperty("line.separator"));
    expected.append("" + System.getProperty("line.separator"));
    expected.append("on matcher \"wtf\"" + System.getProperty("line.separator"));
    expected.append("       1 :     18  : \"wtf\" expected but \"haha\" [IDENTIFIER] found" + System.getProperty("line.separator"));
    expected.append("at rule2" + System.getProperty("line.separator"));
    expected.append("       1 :      8  : hehe huhu " + System.getProperty("line.separator"));
    expected.append("at root" + System.getProperty("line.separator"));
    expected.append("       1 :      0  : bonjour " + System.getProperty("line.separator"));
    expected.append("" + System.getProperty("line.separator"));
    expected.append("Last successful tokens:" + System.getProperty("line.separator"));
    expected.append("-----------------------" + System.getProperty("line.separator"));
    expected.append("  \"huhu\" at 1:13 consumed by rule2" + System.getProperty("line.separator"));
    expected.append("  \"hehe\" at 1:8 consumed by rule2" + System.getProperty("line.separator"));
    expected.append("  \"bonjour\" at 1:0 consumed by root" + System.getProperty("line.separator"));

    assertEquals(baos.toString(), expected.toString());
  }
  
  @Test
  public void okTillEof() {
  	Parser<MyTestGrammar> p = Parser.builder(new MyTestGrammar()).optSetLexer(IdentifierLexer.create())
    		.optAddGrammarDecorator(new MyTestGrammarDecoratorV3()).withMemoizer(false).withExtendedStackTrace(true).build();
		
		try {
		  p.parse("bonjour hehe");
		  throw new IllegalStateException();
		} catch (RecognitionException ex) {
		  
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		p.printStackTrace(new PrintStream(baos)); /* This should not lead to an error */

		StringBuilder expected = new StringBuilder();
    expected.append("Source Snippet:" + System.getProperty("line.separator"));
    expected.append("---------------" + System.getProperty("line.separator"));
    expected.append("  --> bonjour heheEOF" + System.getProperty("line.separator"));
    expected.append("---------------" + System.getProperty("line.separator"));
    expected.append("" + System.getProperty("line.separator"));
    expected.append("on matcher \"next\"" + System.getProperty("line.separator"));
    expected.append("       1 :     12  : and(\"bonjour\", \"hehe\", EOF, \"next\") expected but \"EOF\" [EOF] found" + System.getProperty("line.separator"));
    expected.append("at root" + System.getProperty("line.separator"));
    expected.append("       1 :      0  : bonjour hehe EOF " + System.getProperty("line.separator"));
    expected.append("" + System.getProperty("line.separator"));
    expected.append("Last successful tokens:" + System.getProperty("line.separator"));
    expected.append("-----------------------" + System.getProperty("line.separator"));
    expected.append("  \"hehe\" at 1:8 consumed by root" + System.getProperty("line.separator"));
    expected.append("  \"bonjour\" at 1:0 consumed by root" + System.getProperty("line.separator"));
		
    assertEquals(baos.toString(), expected.toString());
  }

}
