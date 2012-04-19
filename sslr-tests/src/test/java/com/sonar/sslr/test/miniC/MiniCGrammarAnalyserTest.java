/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.analysis.GrammarAnalyser;
import com.sonar.sslr.impl.analysis.GrammarAnalyserStream;
import org.junit.Test;

import java.io.PrintStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MiniCGrammarAnalyserTest {

  @Test
  public void test() {
    Parser<MiniCGrammar> parser = MiniCParser.create();
    GrammarAnalyser analyser = new GrammarAnalyser(parser.getGrammar());
    if (analyser.hasIssues()) {
      GrammarAnalyserStream.print(analyser, new PrintStream(System.err));
    }

    assertThat(analyser.hasIssues(), is(false));
  }

}
