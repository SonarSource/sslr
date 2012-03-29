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

public class AbstractNamingCheckTest {

  private static class Check extends AbstractNamingCheck<MiniCGrammar> {

    @Override
    public Rule[] getRules() {
      return new Rule[] {getContext().getGrammar().binFunctionDefinition, getContext().getGrammar().binVariableDefinition};
    }

    @Override
    public String getName(AstNode astNode) {
      return astNode.getTokenValue();
    }

    @Override
    public String getRegexp() {
      return "[a-z]+";
    }

    @Override
    public String getMessage(String name) {
      return "\"" + name + "\" is a bad name.";
    }

    @Override
    public boolean isExcluded(AstNode astNode) {
      return "LINE".equals(astNode.getTokenValue());
    }

  }

  @Test
  public void detected() {
    setCurrentSourceFile(scanFile("/checks/naming.mc", new Check()));

    assertNumberOfViolations(2);

    assertViolation().atLine(5).withMessage("\"BAD\" is a bad name.");
    assertViolation().atLine(12).withMessage("\"myFunction\" is a bad name.");
  }
}
