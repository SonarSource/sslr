/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.miniC.checks;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import com.sonar.sslr.xpath.AstNodeXpathQuery;

public class IfSMustUseBracesCheckTest extends AbstractCheck {

  private AstNode fileNode;

  @Before
  public void init() {
    fileNode = parse("/checks/ifSMustUseBraces.mc");
    System.out.println(AstXmlPrinter.print(fileNode));
  }

  @Test
  @Ignore
  public void ok() {
    AstNodeXpathQuery<Object> xpath = AstNodeXpathQuery.create(
        "//ifStatement/statement[not(compoundStatement)]/..|//elseClause/statement[not(compoundStatement)]/..");
  }

}
