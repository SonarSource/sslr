/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.till;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.firstOf;
import static org.fest.assertions.Assertions.assertThat;

@Ignore("ExtendedStackTrace will be removed")
public class ExtendedStackTraceTest {

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;
    public Rule rule2;

    @Override
    public Rule getRootRule() {
      return root;
    }

  }

  private class MyTestGrammarDecoratorV1 extends MyTestGrammar {

    public MyTestGrammarDecoratorV1() {
      root.is("bonjour", firstOf(rule2, rule1), and("olaa", "uhu"), EOF);
      rule1.is("hehe");
      rule2.is("hehe", "huhu");
    }
  }

  private class MyTestGrammarDecoratorV2 extends MyTestGrammar {

    public MyTestGrammarDecoratorV2() {
      root.is("bonjour", firstOf(rule2, rule1), and("olaa", "uhu"), EOF);
      rule1.is("hehe");
      rule2.is("hehe", "huhu", "wtf");
    }
  }

  private class MyTestGrammarDecoratorV3 extends MyTestGrammar {

    public MyTestGrammarDecoratorV3() {
      root.is("bonjour", "hehe", EOF, "next");
    }
  }

  private class MyTestGrammarDecoratorTill extends MyTestGrammar {

    public MyTestGrammarDecoratorTill() {
      root.is(till("till"), EOF);
    }
  }

  @Test
  public void ok() {
    ExtendedStackTrace extendedStackTrace = new ExtendedStackTrace();

    Parser<MyTestGrammar> p = Parser.builder((MyTestGrammar) new MyTestGrammarDecoratorV1()).withLexer(IdentifierLexer.create())
        .setParsingEventListeners(extendedStackTrace).build();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      p.parse("bonjour hehe huhu haha");
      throw new IllegalStateException();
    } catch (RecognitionException ex) {
      ExtendedStackTraceStream.print(extendedStackTrace, new PrintStream(baos));
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

    assertThat(baos.toString()).isEqualTo(expected.toString());

    p = Parser.builder((MyTestGrammar) new MyTestGrammarDecoratorV2()).withLexer(IdentifierLexer.create())
        .setParsingEventListeners(extendedStackTrace).build();

    baos = new ByteArrayOutputStream();

    try {
      p.parse("bonjour hehe huhu haha");
      throw new IllegalStateException();
    } catch (RecognitionException ex) {
      ExtendedStackTraceStream.print(extendedStackTrace, new PrintStream(baos));
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

    assertThat(baos.toString()).isEqualTo(expected.toString());
  }

  @Test
  public void okTillEof() {
    ExtendedStackTrace extendedStackTrace = new ExtendedStackTrace();

    Parser<MyTestGrammar> p = Parser.builder((MyTestGrammar) new MyTestGrammarDecoratorV3()).withLexer(IdentifierLexer.create())
        .setParsingEventListeners(extendedStackTrace).build();

    try {
      p.parse("bonjour hehe");
      throw new IllegalStateException();
    } catch (RecognitionException ex) {

    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ExtendedStackTraceStream.print(extendedStackTrace, new PrintStream(baos)); /* This should not lead to an error */

    StringBuilder expected = new StringBuilder();
    expected.append("Source Snippet:" + System.getProperty("line.separator"));
    expected.append("---------------" + System.getProperty("line.separator"));
    expected.append("  --> bonjour heheEOF" + System.getProperty("line.separator"));
    expected.append("---------------" + System.getProperty("line.separator"));
    expected.append("" + System.getProperty("line.separator"));
    expected.append("on matcher \"next\"" + System.getProperty("line.separator"));
    expected.append("       1 :     12  : \"next\" expected but \"EOF\" [EOF] found" + System.getProperty("line.separator"));
    expected.append("at root" + System.getProperty("line.separator"));
    expected.append("       1 :      0  : bonjour hehe EOF " + System.getProperty("line.separator"));
    expected.append("" + System.getProperty("line.separator"));
    expected.append("Last successful tokens:" + System.getProperty("line.separator"));
    expected.append("-----------------------" + System.getProperty("line.separator"));
    expected.append("  \"hehe\" at 1:8 consumed by root" + System.getProperty("line.separator"));
    expected.append("  \"bonjour\" at 1:0 consumed by root" + System.getProperty("line.separator"));

    assertThat(baos.toString()).isEqualTo(expected.toString());
  }

  @Test
  public void testMultilineToken() {
    ExtendedStackTrace extendedStackTrace = new ExtendedStackTrace();

    Parser<MyTestGrammar> p = Parser.builder((MyTestGrammar) new MyTestGrammarDecoratorTill()).withLexer(IdentifierLexer.create())
        .setParsingEventListeners(extendedStackTrace).build();

    try {
      p.parse("hehe\nhaha!\n\n!\nhuhu till BANG");
      throw new IllegalStateException();
    } catch (RecognitionException ex) {

    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedStackTraceStream.print(extendedStackTrace, new PrintStream(baos));
    String actual = baos.toString();

    StringBuilder expected = new StringBuilder();
    expected.append("Source Snippet:" + System.getProperty("line.separator"));
    expected.append("---------------" + System.getProperty("line.separator"));
    expected.append("    1 hehe" + System.getProperty("line.separator"));
    expected.append("    2 haha!" + System.getProperty("line.separator"));
    expected.append("    3 " + System.getProperty("line.separator"));
    expected.append("    4 !" + System.getProperty("line.separator"));
    expected.append("  --> huhu till BANGEOF" + System.getProperty("line.separator"));

    assertThat(actual.startsWith(expected.toString())).isTrue();
  }

}
