/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2021 SonarSource SA
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
package org.sonar.sslr.internal.vm;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.sonar.sslr.grammar.GrammarException;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

// TODO this test should also check state of machine after execution
public class MachineIntegrationTest {

  @Rule
  public Timeout timeout = new Timeout(5000);

  @Test
  public void pattern() {
    Instruction[] instructions = new PatternExpression("foo|bar").compile(new CompilationHandler());
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isTrue();
    assertThat(Machine.execute("baz", instructions)).isFalse();
  }

  @Test
  public void string() {
    Instruction[] instructions = new StringExpression("foo").compile(new CompilationHandler());
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isFalse();
  }

  @Test
  public void sequence() {
    Instruction[] instructions = new SequenceExpression(
        new StringExpression("foo"), new StringExpression("bar")).compile(new CompilationHandler());
    assertThat(Machine.execute("foobar", instructions)).isTrue();
    assertThat(Machine.execute("baz", instructions)).isFalse();
  }

  @Test
  public void firstOf() {
    Instruction[] instructions = new FirstOfExpression(
        new StringExpression("foo"),
        new StringExpression("bar"),
        new StringExpression("baz")).compile(new CompilationHandler());
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isTrue();
    assertThat(Machine.execute("baz", instructions)).isTrue();
    assertThat(Machine.execute("qux", instructions)).isFalse();
  }

  @Test
  public void optional() {
    Instruction[] instructions = new OptionalExpression(new StringExpression("a")).compile(new CompilationHandler());
    assertThat(Machine.execute("", instructions)).isTrue();
    assertThat(Machine.execute("a", instructions)).isTrue();
  }

  @Test
  public void next() {
    Instruction[] instructions = new NextExpression(new StringExpression("foo")).compile(new CompilationHandler());
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isFalse();
  }

  @Test
  public void nextNot() {
    Instruction[] instructions = new NextNotExpression(new StringExpression("foo")).compile(new CompilationHandler());
    assertThat(Machine.execute("foo", instructions)).isFalse();
    assertThat(Machine.execute("bar", instructions)).isTrue();
  }

  @Test
  public void zeroOrMore() {
    Instruction[] instructions = new ZeroOrMoreExpression(new StringExpression("a")).compile(new CompilationHandler());
    assertThat(Machine.execute("", instructions)).isTrue();
    assertThat(Machine.execute("a", instructions)).isTrue();
    assertThat(Machine.execute("aa", instructions)).isTrue();
  }

  @Test
  public void zeroOrMore_should_not_cause_infinite_loop() {
    Instruction[] instructions = new ZeroOrMoreExpression(
        new FirstOfExpression(
            new StringExpression("foo"),
            new StringExpression(""))).compile(new CompilationHandler());
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> Machine.execute("foo", instructions));
    assertEquals("The inner part of ZeroOrMore and OneOrMore must not allow empty matches", thrown.getMessage());
  }

  @Test
  public void oneOrMore() {
    Instruction[] instructions = new OneOrMoreExpression(new StringExpression("a")).compile(new CompilationHandler());
    assertThat(Machine.execute("", instructions)).isFalse();
    assertThat(Machine.execute("a", instructions)).isTrue();
    assertThat(Machine.execute("aa", instructions)).isTrue();
  }

  @Test
  public void oneOrMore_should_not_cause_infinite_loop() {
    Instruction[] instructions = new OneOrMoreExpression(
        new FirstOfExpression(
            new StringExpression("foo"),
            new StringExpression(""))).compile(new CompilationHandler());
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> Machine.execute("foo", instructions));
    assertEquals("The inner part of ZeroOrMore and OneOrMore must not allow empty matches", thrown.getMessage());
  }

  @Test
  public void token() {
    Instruction[] instructions = new TokenExpression(GenericTokenType.IDENTIFIER, new StringExpression("foo")).compile(new CompilationHandler());
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isFalse();
  }

  @Test
  public void trivia() {
    Instruction[] instructions = new TriviaExpression(TriviaKind.COMMENT, new StringExpression("foo")).compile(new CompilationHandler());
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isFalse();
  }

}
