/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.OneOrMoreExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.StringExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
public class OneOrMoreExpressionBenchmark {

  private String input;
  private Instruction[] oneOrMore;
  private Instruction[] usingZeroOrMore;

  @Setup
  public void setup() {
    int n = Integer.getInteger("n", 3);
    input = Strings.repeat("t", n);
    ParsingExpression subExpression = new StringExpression("t");
    oneOrMore = compile(new OneOrMoreExpression(subExpression));
    usingZeroOrMore = compile(new SequenceExpression(subExpression, new ZeroOrMoreExpression(subExpression)));
  }

  @Benchmark
  public boolean oneOrMore() {
    return Machine.execute(input, oneOrMore);
  }

  @Benchmark
  public boolean usingZeroOrMore() {
    return Machine.execute(input, usingZeroOrMore);
  }

  private static Instruction[] compile(ParsingExpression expression) {
    return new SequenceExpression(expression, EndOfInputExpression.INSTANCE).compile(new CompilationHandler());
  }

}
