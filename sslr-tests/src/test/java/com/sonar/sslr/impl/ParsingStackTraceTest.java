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
package com.sonar.sslr.impl;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.till;
import static com.sonar.sslr.test.lexer.TokenUtils.lex;
import static org.fest.assertions.Assertions.assertThat;

public class ParsingStackTraceTest {

  private List<Token> tokens;
  private ParsingState state;
  private final RuleMatcher compilationUnit = ((RuleDefinition) new JavaGrammar().getRootRule()).getRule();

  @Before
  public void init() {
    tokens = lex("package com.test;\n" +
        "import java.util.*;\n" +
        "public abstract clas MyClass {\n" +
        "   public abstract void run();\n" +
        "}\n");

    Token lastToken = tokens.remove(tokens.size() - 1);
    lastToken = Token.builder(lastToken)
        .setCopyBook("file1", 10)
        .build();

    tokens.add(lastToken);

    state = new ParsingState(tokens);
  }

  @Test
  public void testGenerateFullStackTrace() {
    compilationUnit.isMatching(state);

    StringBuilder expected = new StringBuilder();
    expected.append("------\n");
    expected.append("    1 package com.test;\n");
    expected.append("    2 import java.util.*;\n");
    expected.append("-->   public abstract clas MyClass {\n");
    expected.append("    4    public abstract void run();\n");
    expected.append("    5 }\n");
    expected.append("------\n");
    expected.append("Expected : <\"class\"> but was : <clas [IDENTIFIER]> ('tests://unittest': Line 3 / Column 16)\n");

    assertThat(ParsingStackTrace.generateFullStackTrace(state)).isEqualTo(expected.toString());
  }

  @Test
  public void testGenerateErrorOnCopyBook() {
    compilationUnit.isMatching(state);

    int outpostTokenIndex = state.getOutpostMatcherTokenIndex();

    List<Token> modifiedTokens = Lists.newArrayList(tokens);
    Token outpostToken = modifiedTokens.remove(outpostTokenIndex);
    Token modifiedOutpostToken = Token.builder(outpostToken)
        .setCopyBook("file1", 20)
        .build();
    modifiedTokens.add(outpostTokenIndex, modifiedOutpostToken);

    ParsingState modifiedState = new ParsingState(modifiedTokens);

    compilationUnit.isMatching(modifiedState);

    StringBuilder expected = new StringBuilder();
    expected
        .append("Expected : <\"class\"> but was : <clas [IDENTIFIER]> (copy book 'tests://unittest': Line 3 / Column 16 called from file 'file1': Line 20)\n");

    assertThat(ParsingStackTrace.generate(modifiedState)).isEqualTo(expected.toString());
  }

  @Test
  public void testGenerateFullStackTraceWhenEndOfFileIsReached() {
    tokens = lex("package com.test;\n" + "import java.util.*;\n" + "public abstract");
    state = new ParsingState(tokens);
    compilationUnit.isMatching(state);

    StringBuilder expected = new StringBuilder();
    expected.append("------\n");
    expected.append("    1 package com.test;\n");
    expected.append("    2 import java.util.*;\n");
    expected.append("-->   public abstract\n");
    expected.append("------\n");
    expected.append("Expected : <\"class\"> but was : <EOF> ('tests://unittest')\n");

    assertThat(ParsingStackTrace.generateFullStackTrace(state)).isEqualTo(expected.toString());
  }

  public class JavaGrammar extends Grammar {

    public Rule compilationUnit;
    public Rule packageDeclaration;
    public Rule importDeclaration;
    public Rule classBlock;
    public Rule classDeclaration;

    public JavaGrammar() {
      compilationUnit.is(packageDeclaration, importDeclaration, classBlock);
      classBlock.is(classDeclaration);

      packageDeclaration.is("package", till(";"));
      importDeclaration.is("import", till(";"));
      classDeclaration.is("public", "abstract", "class");
    }

    @Override
    public Rule getRootRule() {
      return compilationUnit;
    }
  }
}
