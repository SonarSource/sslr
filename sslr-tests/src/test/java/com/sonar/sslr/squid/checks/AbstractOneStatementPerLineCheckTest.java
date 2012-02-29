/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

public class AbstractOneStatementPerLineCheckTest {

  private static class Check extends AbstractOneStatementPerLineCheck<MiniCGrammar> {

    @Override
    public Rule getStatementRule() {
      return getContext().getGrammar().statement;
    }

    @Override
    public boolean isExcluded(AstNode statementNode) {
      return statementNode.getChild(0).is(getContext().getGrammar().compoundStatement);
    }

  }

  @Test
  public void detected() {
    setCurrentSourceFile(scanFile("/checks/one_statement_per_line.mc", new Check()));

    assertOnlyOneViolation().atLine(7).withMessage("At most one statement is allowed per line, but 2 statements were found on this line.");
  }

}
