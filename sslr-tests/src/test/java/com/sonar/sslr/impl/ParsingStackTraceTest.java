/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.test.lexer.TokenUtils.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;

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

    assertEquals(expected.toString(), ParsingStackTrace.generateFullStackTrace(state));
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

    assertEquals(expected.toString(), ParsingStackTrace.generate(modifiedState));
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

    assertEquals(expected.toString(), ParsingStackTrace.generateFullStackTrace(state));
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
