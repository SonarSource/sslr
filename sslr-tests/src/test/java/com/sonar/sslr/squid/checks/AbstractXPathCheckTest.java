/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

public class AbstractXPathCheckTest {

  private static class EmptyXPathCheck extends AbstractXPathCheck<MiniCGrammar> {

    @Override
    public String getXPathQuery() {
      return "";
    }

    @Override
    public String getMessage() {
      return "Empty XPath check.";
    }

  }

  @Test
  public void emptyXPathCheck() {
    setCurrentSourceFile(scanFile("/checks/xpath.mc", new EmptyXPathCheck()));

    assertNoViolation();
  }

  private static class BooleanXPathCheckWithResults extends AbstractXPathCheck<MiniCGrammar> {

    @Override
    public String getXPathQuery() {
      return "count(//variableDefinition) > 0";
    }

    @Override
    public String getMessage() {
      return "Boolean XPath rule with results.";
    }

  }

  @Test
  public void booleanXPathCheckWithResults() {
    setCurrentSourceFile(scanFile("/checks/xpath.mc", new BooleanXPathCheckWithResults()));

    assertNumberOfViolations(1);

    assertViolation().withMessage("Boolean XPath rule with results.");
  }

  private static class BooleanXPathCheckWithoutResults extends AbstractXPathCheck<MiniCGrammar> {

    @Override
    public String getXPathQuery() {
      return "count(//variableDefinition) > 2";
    }

    @Override
    public String getMessage() {
      return "Boolean XPath rule without results.";
    }

  }

  @Test
  public void booleanXPathCheckWithoutResults() {
    setCurrentSourceFile(scanFile("/checks/xpath.mc", new BooleanXPathCheckWithoutResults()));

    assertNoViolation();
  }

  private static class AstNodesXpathCheck extends AbstractXPathCheck<MiniCGrammar> {

    @Override
    public String getXPathQuery() {
      return "//variableDefinition";
    }

    @Override
    public String getMessage() {
      return "No variable definitions allowed!";
    }

  }

  @Test
  public void astNodesXpathCheck() {
    setCurrentSourceFile(scanFile("/checks/xpath.mc", new AstNodesXpathCheck()));

    assertNumberOfViolations(2);

    assertViolation().atLine(1).withMessage("No variable definitions allowed!");
    assertViolation().atLine(5);
  }

}
