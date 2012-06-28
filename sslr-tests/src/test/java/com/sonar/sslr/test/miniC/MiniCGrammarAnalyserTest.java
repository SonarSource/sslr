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
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.analysis.GrammarAnalyser;
import com.sonar.sslr.impl.analysis.GrammarAnalyserStream;
import org.junit.Test;

import java.io.PrintStream;

import static org.fest.assertions.Assertions.assertThat;

public class MiniCGrammarAnalyserTest {

  @Test
  public void test() {
    Parser<MiniCGrammar> parser = MiniCParser.create();
    GrammarAnalyser analyser = new GrammarAnalyser(parser.getGrammar());
    if (analyser.hasIssues()) {
      GrammarAnalyserStream.print(analyser, new PrintStream(System.err));
    }

    assertThat(analyser.hasIssues()).isFalse();
  }

}
