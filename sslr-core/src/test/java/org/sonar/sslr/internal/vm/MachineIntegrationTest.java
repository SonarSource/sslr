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
package org.sonar.sslr.internal.vm;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MachineIntegrationTest {

  @Rule
  public Timeout timeout = new Timeout(5000);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void pattern() {
    Instruction[] instructions = new PatternExpression("foo|bar").compile();
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isTrue();
    assertThat(Machine.execute("baz", instructions)).isFalse();
  }

  @Test
  public void string() {
    Instruction[] instructions = new StringExpression("foo").compile();
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isFalse();
  }

  @Test
  public void sequence() {
    Instruction[] instructions = new SequenceExpression(
        new StringExpression("foo"), new StringExpression("bar")).compile();
    assertThat(Machine.execute("foobar", instructions)).isTrue();
    assertThat(Machine.execute("baz", instructions)).isFalse();
  }

  @Test
  public void firstOf() {
    Instruction[] instructions = new FirstOfExpression(
        new StringExpression("foo"),
        new StringExpression("bar"),
        new StringExpression("baz")).compile();
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isTrue();
    assertThat(Machine.execute("baz", instructions)).isTrue();
    assertThat(Machine.execute("qux", instructions)).isFalse();
  }

  @Test
  public void optional() {
    Instruction[] instructions = new OptionalExpression(new StringExpression("a")).compile();
    assertThat(Machine.execute("", instructions)).isTrue();
    assertThat(Machine.execute("a", instructions)).isTrue();
  }

  @Test
  public void next() {
    Instruction[] instructions = new NextExpression(new StringExpression("foo")).compile();
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isFalse();
  }

  @Test
  public void nextNot() {
    Instruction[] instructions = new NextNotExpression(new StringExpression("foo")).compile();
    assertThat(Machine.execute("foo", instructions)).isFalse();
    assertThat(Machine.execute("bar", instructions)).isTrue();
  }

  @Test
  public void zeroOrMore() {
    Instruction[] instructions = new ZeroOrMoreExpression(new StringExpression("a")).compile();
    assertThat(Machine.execute("", instructions)).isTrue();
    assertThat(Machine.execute("a", instructions)).isTrue();
    assertThat(Machine.execute("aa", instructions)).isTrue();
  }

  @Test
  public void oneOrMore() {
    Instruction[] instructions = new OneOrMoreExpression(new StringExpression("a")).compile();
    assertThat(Machine.execute("", instructions)).isFalse();
    assertThat(Machine.execute("a", instructions)).isTrue();
    assertThat(Machine.execute("aa", instructions)).isTrue();
  }

  @Test
  public void token() {
    Instruction[] instructions = new TokenExpression(GenericTokenType.IDENTIFIER, new StringExpression("foo")).compile();
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isFalse();
  }

  @Test
  public void trivia() {
    Instruction[] instructions = new TriviaExpression(TriviaKind.COMMENT, new StringExpression("foo")).compile();
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isFalse();
  }

  /**
   * SSLR-278
   */
  @Test
  public void should_not_cause_StackOverflowError() {
    VmGrammarBuilder b = VmGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("(", ruleKey, ")");
    CompiledGrammar grammar = b.build();

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 100000; i++) {
      sb.append("(");
    }
    Machine.parse(sb.toString(), grammar, ruleKey);
  }

  /**
   * SSLR-220
   */
  @Test
  public void should_detect_immediate_left_recursion() {
    CompiledGrammar grammar = ImmediateLeftRecursion.create();
    thrown.expect(GrammarException.class);
    thrown.expectMessage("Left recursion has been detected, involved rule: " + ImmediateLeftRecursion.A);
    Machine.parse("", grammar, ImmediateLeftRecursion.A);
  }

  private enum ImmediateLeftRecursion implements GrammarRuleKey {
    A,
    T;

    private static CompiledGrammar create() {
      VmGrammarBuilder b = new VmGrammarBuilder();
      b.rule(A).is(b.firstOf(
          b.sequence(A, T),
          T));
      b.rule(T).is("t");
      return b.build();
    }
  }

  /**
   * SSLR-220
   */
  @Test
  public void should_detect_indirect_left_recursion() {
    CompiledGrammar grammar = IndirectLeftRecursion.create();
    thrown.expect(GrammarException.class);
    thrown.expectMessage("Left recursion has been detected, involved rule: " + IndirectLeftRecursion.B);
    Machine.parse("", grammar, IndirectLeftRecursion.A);
  }

  private enum IndirectLeftRecursion implements GrammarRuleKey {
    A,
    T1,
    S1,
    B,
    T2,
    S2;

    private static CompiledGrammar create() {
      VmGrammarBuilder b = new VmGrammarBuilder();
      b.rule(A).is(b.firstOf(
          b.sequence(B, T1),
          S1));
      b.rule(B).is(b.firstOf(
          b.sequence(A, T2),
          S2));
      b.rule(T1).is("t1");
      b.rule(S1).is("s1");
      b.rule(T2).is("t2");
      b.rule(S2).is("s2");
      return b.build();
    }
  }

}
