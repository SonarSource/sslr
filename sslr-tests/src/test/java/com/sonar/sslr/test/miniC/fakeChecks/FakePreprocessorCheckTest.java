/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC.fakeChecks;

import org.junit.Test;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.PreprocessingDirective;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.squid.checks.SquidCheck;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCPreprocessor;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;
import static com.sonar.sslr.test.squid.CheckMatchers.assertOnlyOneViolation;
import static com.sonar.sslr.test.squid.CheckMatchers.setCurrentSourceFile;

public class FakePreprocessorCheckTest {

  private class FakePreprocessorCheck extends SquidCheck<MiniCGrammar> implements AstAndTokenVisitor {

    public void visitToken(Token token) {
      for (Trivia trivia : token.getTrivia()) {
        if (trivia.isPreprocessor() && trivia.hasDirective()) {
          PreprocessingDirective directive = trivia.getPreprocessingDirective();
          AstNode preprocessorStructure = directive.getAst();
          MiniCPreprocessor.MiniCPreprocessorGrammar grammar = (MiniCPreprocessor.MiniCPreprocessorGrammar) directive.getGrammar();
          AstNode definition = preprocessorStructure.findFirstDirectChild(grammar.binDefinition);
          if (definition != null && "WTF".equals(definition.getTokenOriginalValue())) {
            getContext().createLineViolation(this, "Be gentle in your preprocessor definitions.",
                trivia.getPreprocessingDirective().getAst().getTokenLine());
          }
        }
      }
    }

  }

  @Test
  public void testFakeCommentCheck() {
    setCurrentSourceFile(scanFile("/fakeChecks/fakePreprocessor.mc", new FakePreprocessorCheck()));

    assertOnlyOneViolation().atLine(2);
  }

}
