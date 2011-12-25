/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCLexer;

public class AbstractMagicCheckTest {

  private static class MagicCheck extends AbstractMagicCheck<MiniCGrammar> {

    @Override
    public AstNodeType[] getPatterns() {
      return new AstNodeType[] { MiniCLexer.Literals.INTEGER };
    }

    @Override
    public AstNodeType[] getInclusions() {
      return new AstNodeType[] { getContext().getGrammar().whileStatement };
    }

    @Override
    public AstNodeType[] getExclusions() {
      return new AstNodeType[] { getContext().getGrammar().variableInitializer };
    }

    @Override
    public String getMessage() {
      return "Avoid magic stuff.";
    }

  }

  @Test
  public void magic() {
    setCurrentSourceFile(scanFile("/checks/magic.mc", new MagicCheck()));

    assertNumberOfViolations(2);

    assertViolation().atLine(5).withMessage("Avoid magic stuff.");
    assertViolation().atLine(9);
  }

}
