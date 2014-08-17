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
package org.sonar.sslr.benchmarks;

import com.google.common.base.Strings;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.parser.ParseRunner;

import java.util.Arrays;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
public class MemoizationOfMismatchesBenchmark {

  private ParseRunner required, notRequired;
  private char[] input;

  @Setup
  public void setup() {
    int k = Integer.getInteger("k", 5);
    int n = Integer.getInteger("n", 10);
    input = Strings.repeat(" k" + (k - 1), n).toCharArray();

    GrammarRuleKey root = newRuleKey();
    GrammarRuleKey spacing = newRuleKey();
    GrammarRuleKey[] rules = new GrammarRuleKey[k];
    for (int i = 0; i < k; i++) {
      rules[i] = newRuleKey();
    }

    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    b.rule(root).is(b.zeroOrMore(b.firstOf(rules[0], rules[1], Arrays.copyOfRange(rules, 2, rules.length))), b.endOfInput());
    b.rule(spacing).is(" ");
    for (int i = 0; i < k; i++) {
      b.rule(rules[i]).is(b.optional(spacing), "k" + i);
    }
    required = new ParseRunner(b.build().rule(root));

    b = LexerlessGrammarBuilder.create();
    b.rule(root).is(b.zeroOrMore(b.firstOf(rules[0], rules[1], Arrays.copyOfRange(rules, 2, rules.length))), b.endOfInput());
    b.rule(spacing).is(b.optional(" "));
    for (int i = 0; i < k; i++) {
      b.rule(rules[i]).is(spacing, "k" + i);
    }
    notRequired = new ParseRunner(b.build().rule(root));
  }

  @Benchmark
  public boolean required() {
    return required.parse(input).isMatched();
  }

  @Benchmark
  public boolean notRequired() {
    return notRequired.parse(input).isMatched();
  }

  private static GrammarRuleKey newRuleKey() {
    return new GrammarRuleKey() {
    };
  }

}
